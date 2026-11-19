package br.ucsal.horas.servico;

import br.ucsal.horas.dominio.Aluno;
import br.ucsal.horas.dominio.Anexo;
import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.dominio.TipoDocumento;
import br.ucsal.horas.eventos.EventoStatus;
import br.ucsal.horas.eventos.OuvinteStatus;
import br.ucsal.horas.excecao.RegraNegocioException;
import br.ucsal.horas.repositorio.RepositorioSolicitacoes;
import br.ucsal.horas.util.CalendarioUtil;
import br.ucsal.horas.util.Formatos;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Fachada (Facade) com todas as operações do aluno e a simulação do NDE.
 * O {@link Clock} é injetado para que prazos possam ser testados sem esperar dias reais.
 */
public class GestorHoras {

    public static final int PRAZO_ANEXOS_DIAS_UTEIS = 5;
    public static final int MINIMO_MODALIDADES = 2;

    private final Aluno aluno;
    private final RepositorioSolicitacoes repositorio;
    private final Clock relogio;
    private final AnalisadorNDE analisador = new AnalisadorNDE();
    private final List<OuvinteStatus> ouvintes = new CopyOnWriteArrayList<>();

    public GestorHoras(Aluno aluno, RepositorioSolicitacoes repositorio, Clock relogio) {
        this.aluno = aluno;
        this.repositorio = repositorio;
        this.relogio = relogio;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    public void adicionarOuvinte(OuvinteStatus ouvinte) {
        ouvintes.add(ouvinte);
    }

    // ---------------------------------------------------------------- consultas

    /** Modalidades que ainda aceitam horas (as já completas não são exibidas, como no portal). */
    public List<Modalidade> modalidadesDisponiveis() {
        return Arrays.stream(Modalidade.values())
                .filter(m -> horasAproveitadas(m) < m.getLimiteHoras())
                .toList();
    }

    public int horasAproveitadas(Modalidade modalidade) {
        return repositorio.listar().stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.DEFERIDA && s.getModalidade() == modalidade)
                .mapToInt(Solicitacao::getHorasAproveitadas)
                .sum();
    }

    public List<Solicitacao> listarSolicitacoes() {
        atualizarPrazos();
        return repositorio.listar();
    }

    public List<Solicitacao> listarPorStatus(StatusSolicitacao status) {
        return listarSolicitacoes().stream().filter(s -> s.getStatus() == status).toList();
    }

    public Solicitacao buscar(int id) {
        return repositorio.buscar(id)
                .orElseThrow(() -> new RegraNegocioException("Solicitação #" + id + " não encontrada."));
    }

    public ResumoProgresso resumo() {
        atualizarPrazos();
        List<Solicitacao> todas = repositorio.listar();

        var porModalidade = todas.stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.DEFERIDA)
                .collect(Collectors.groupingBy(Solicitacao::getModalidade,
                        () -> new EnumMap<>(Modalidade.class),
                        Collectors.summingInt(Solicitacao::getHorasAproveitadas)));

        var porStatus = todas.stream()
                .collect(Collectors.groupingBy(Solicitacao::getStatus,
                        () -> new EnumMap<>(StatusSolicitacao.class),
                        Collectors.counting()));

        int total = porModalidade.values().stream().mapToInt(Integer::intValue).sum();
        return new ResumoProgresso(aluno, total, porModalidade, porStatus);
    }

    // ---------------------------------------------------------------- operações do aluno

    public Solicitacao criarSolicitacao(SolicitacaoBuilder dados) {
        atualizarPrazos();
        dados.validar();

        Modalidade modalidade = dados.getModalidade();
        if (dados.getDataFinal().isAfter(hoje())) {
            throw new RegraNegocioException("A atividade precisa ter sido efetivamente realizada: "
                    + "a data final (" + Formatos.DATA.format(dados.getDataFinal()) + ") está no futuro.");
        }
        if (!modalidadesDisponiveis().contains(modalidade)) {
            throw new RegraNegocioException("A modalidade \"" + modalidade.getRotulo()
                    + "\" já atingiu o limite de " + modalidade.getLimiteHoras() + "h.");
        }
        boolean jaAberta = repositorio.listar().stream()
                .anyMatch(s -> s.getModalidade() == modalidade && s.getStatus().isAberta());
        if (jaAberta) {
            throw new RegraNegocioException("Já existe uma solicitação em andamento para \""
                    + modalidade.getRotulo() + "\". Deve ser feita uma solicitação por modalidade.");
        }

        LocalDate hoje = hoje();
        Solicitacao solicitacao = dados.build(repositorio.proximoId(), hoje,
                CalendarioUtil.somarDiasUteis(hoje, PRAZO_ANEXOS_DIAS_UTEIS));
        repositorio.salvar(solicitacao);
        publicar(solicitacao, null, "prazo para anexar: " + Formatos.DATA.format(solicitacao.getPrazoAnexos()));
        return solicitacao;
    }

    public void anexarDocumento(int id, TipoDocumento tipo, String nomeArquivo) {
        atualizarPrazos();
        Solicitacao s = buscar(id);
        Anexo anexo = new Anexo(tipo, nomeArquivo); // valida a elegibilidade do arquivo

        if (s.getStatus() != StatusSolicitacao.AGUARDANDO_ANEXOS) {
            throw new RegraNegocioException("Só é possível anexar documentos enquanto a solicitação está "
                    + "\"Aguardando anexos\" (situação atual: " + s.getStatus().getRotulo() + ").");
        }
        if (!s.getModalidade().getDocumentosExigidos().contains(tipo)) {
            throw new RegraNegocioException("O documento \"" + tipo.getRotulo() + "\" não é exigido para "
                    + s.getModalidade().getRotulo() + ".");
        }
        if (s.documentosFaltantes().contains(tipo)) {
            s.anexar(anexo);
        } else {
            throw new RegraNegocioException("O documento \"" + tipo.getRotulo() + "\" já foi anexado.");
        }
    }

    /**
     * Envia a solicitação ao NDE. Exige ao menos um anexo; se faltar documento obrigatório,
     * o envio é aceito (como no portal), mas o NDE tende a indeferir, e por isso os faltantes são devolvidos.
     */
    public Set<TipoDocumento> enviarParaAnalise(int id) {
        atualizarPrazos();
        Solicitacao s = buscar(id);
        if (s.getStatus() != StatusSolicitacao.AGUARDANDO_ANEXOS) {
            throw new RegraNegocioException("A solicitação #" + id + " não está aguardando anexos (situação atual: "
                    + s.getStatus().getRotulo() + ").");
        }
        if (s.getAnexos().isEmpty()) {
            throw new RegraNegocioException("Anexe ao menos um documento antes de enviar para análise.");
        }
        StatusSolicitacao anterior = s.getStatus();
        s.transitarPara(StatusSolicitacao.EM_ANALISE, "Enviada ao NDE.");
        publicar(s, anterior, "");
        return s.documentosFaltantes();
    }

    // ---------------------------------------------------------------- NDE

    public Solicitacao analisar(int id) {
        Solicitacao s = buscar(id);
        if (s.getStatus() != StatusSolicitacao.EM_ANALISE) {
            throw new RegraNegocioException("A solicitação #" + id + " não está em análise (situação atual: "
                    + s.getStatus().getRotulo() + ").");
        }
        ResultadoAnalise resultado = analisador.analisar(s, horasAproveitadas(s.getModalidade()));
        StatusSolicitacao anterior = s.getStatus();
        if (resultado.deferida()) {
            s.definirHorasAproveitadas(resultado.horas());
            s.transitarPara(StatusSolicitacao.DEFERIDA, resultado.parecer());
        } else {
            s.transitarPara(StatusSolicitacao.INDEFERIDA, resultado.parecer());
        }
        publicar(s, anterior, resultado.parecer());
        return s;
    }

    public List<Solicitacao> analisarPendentes() {
        return listarPorStatus(StatusSolicitacao.EM_ANALISE).stream()
                .map(s -> analisar(s.getId()))
                .toList();
    }

    // ---------------------------------------------------------------- prazos

    /** Cancela as solicitações que passaram de 5 dias úteis sem anexos. Devolve quantas foram canceladas. */
    public int atualizarPrazos() {
        List<Solicitacao> vencidas = repositorio.listar().stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.AGUARDANDO_ANEXOS)
                .filter(s -> hoje().isAfter(s.getPrazoAnexos()))
                .toList();
        for (Solicitacao s : vencidas) {
            StatusSolicitacao anterior = s.getStatus();
            String motivo = "Prazo de " + PRAZO_ANEXOS_DIAS_UTEIS + " dias úteis para anexar documentos expirou em "
                    + Formatos.DATA.format(s.getPrazoAnexos()) + ".";
            s.transitarPara(StatusSolicitacao.CANCELADA, motivo);
            publicar(s, anterior, motivo);
        }
        return vencidas.size();
    }

    private void publicar(Solicitacao s, StatusSolicitacao anterior, String detalhe) {
        EventoStatus evento = new EventoStatus(s.getId(), s.getModalidade(), anterior, s.getStatus(), hoje(), detalhe);
        ouvintes.forEach(o -> o.aoMudarStatus(evento));
    }
}
