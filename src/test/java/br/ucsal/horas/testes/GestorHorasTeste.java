package br.ucsal.horas.testes;

import br.ucsal.horas.dominio.Aluno;
import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.dominio.TipoDocumento;
import br.ucsal.horas.eventos.HistoricoEventos;
import br.ucsal.horas.excecao.RegraNegocioException;
import br.ucsal.horas.repositorio.RepositorioEmMemoria;
import br.ucsal.horas.servico.GestorHoras;
import br.ucsal.horas.servico.ResumoProgresso;

import java.time.LocalDate;

import static br.ucsal.horas.testes.Verificacoes.contem;
import static br.ucsal.horas.testes.Verificacoes.igual;
import static br.ucsal.horas.testes.Verificacoes.lanca;
import static br.ucsal.horas.testes.Verificacoes.verdadeiro;

/** Testa o fluxo principal e as regras de negócio pela fachada, como o menu faz. */
public class GestorHorasTeste {

    private final LocalDate hoje = LocalDate.of(2026, 11, 16); // segunda-feira
    private final RelogioMutavel relogio = new RelogioMutavel(hoje);
    private final HistoricoEventos historico = new HistoricoEventos();
    private final GestorHoras gestor =
    		new GestorHoras(new Aluno("Aluno Teste", "T-1", "Curso Teste", 3200, 200), new RepositorioEmMemoria(), relogio);

    public GestorHorasTeste() {
        gestor.adicionarOuvinte(historico);
    }

    private SolicitacaoBuilder evento(int total, int pedido) {
        return new SolicitacaoBuilder().modalidade(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS)
                .descricao("Evento de teste").periodo(hoje.minusDays(10), hoje.minusDays(9))
                .cargaTotal(total).cargaSolicitada(pedido);
    }

    private Solicitacao eventoDeferido(int horas) {
        Solicitacao s = gestor.criarSolicitacao(evento(horas, horas));
        gestor.anexarDocumento(s.getId(), TipoDocumento.CERTIFICADO, "cert.pdf");
        gestor.enviarParaAnalise(s.getId());
        return gestor.analisar(s.getId());
    }

    private Solicitacao monitoriaDeferida(int horas) {
        Solicitacao s = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.MONITORIA)
                .descricao("Monitoria de teste").periodo(hoje.minusDays(40), hoje.minusDays(10))
                .cargaTotal(horas).cargaSolicitada(horas));
        gestor.anexarDocumento(s.getId(), TipoDocumento.RELATORIO_ALUNO, "rel.pdf");
        gestor.anexarDocumento(s.getId(), TipoDocumento.RELATORIO_ORIENTADOR, "parecer.pdf");
        gestor.enviarParaAnalise(s.getId());
        return gestor.analisar(s.getId());
    }

    @Teste("fluxo principal: criar, anexar, enviar e ser deferida pelo NDE")
    public void fluxoPrincipalDeferido() {
        Solicitacao s = eventoDeferido(20);
        igual(StatusSolicitacao.DEFERIDA, s.getStatus());
        igual(20, s.getHorasAproveitadas());
        igual(20, gestor.horasAproveitadas(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS));
    }

    @Teste("envio com documento obrigatório faltando é indeferido pelo NDE")
    public void indeferidaPorDocumentoFaltante() {
        Solicitacao s = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.MONITORIA)
                .descricao("Monitoria").periodo(hoje.minusDays(40), hoje.minusDays(10))
                .cargaTotal(60).cargaSolicitada(60));
        gestor.anexarDocumento(s.getId(), TipoDocumento.RELATORIO_ALUNO, "rel.pdf");
        var faltantes = gestor.enviarParaAnalise(s.getId());
        verdadeiro(faltantes.contains(TipoDocumento.RELATORIO_ORIENTADOR), "deveria avisar do documento faltante");

        Solicitacao julgada = gestor.analisar(s.getId());
        igual(StatusSolicitacao.INDEFERIDA, julgada.getStatus());
        igual(0, julgada.getHorasAproveitadas());
        contem(julgada.getParecer(), "ausentes");
    }

    @Teste("não é possível enviar para análise sem nenhum anexo")
    public void envioSemAnexo() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        var e = lanca(RegraNegocioException.class, () -> gestor.enviarParaAnalise(s.getId()));
        contem(e.getMessage(), "ao menos um documento");
    }

    @Teste("arquivo inelegível (.exe) é recusado e nada é anexado")
    public void arquivoInelegivel() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        lanca(RegraNegocioException.class, () -> gestor.anexarDocumento(s.getId(), TipoDocumento.CERTIFICADO, "x.exe"));
        igual(0, s.getAnexos().size());
    }

    @Teste("documento que a modalidade não exige é recusado")
    public void documentoNaoExigido() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        var e = lanca(RegraNegocioException.class,
                () -> gestor.anexarDocumento(s.getId(), TipoDocumento.TERMO_ESTAGIO, "termo.pdf"));
        contem(e.getMessage(), "não é exigido");
    }

    @Teste("o mesmo tipo de documento não pode ser anexado duas vezes")
    public void documentoDuplicado() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        gestor.anexarDocumento(s.getId(), TipoDocumento.CERTIFICADO, "a.pdf");
        lanca(RegraNegocioException.class, () -> gestor.anexarDocumento(s.getId(), TipoDocumento.CERTIFICADO, "b.pdf"));
    }

    @Teste("passados 5 dias úteis sem anexos, a solicitação é cancelada")
    public void cancelamentoPorPrazo() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        igual(LocalDate.of(2026, 11, 23), s.getPrazoAnexos()); // segunda + 5 dias úteis
        relogio.avancarDias(8);
        igual(1, gestor.atualizarPrazos());
        igual(StatusSolicitacao.CANCELADA, s.getStatus());
        contem(s.getParecer(), "expirou");
        lanca(RegraNegocioException.class, () -> gestor.anexarDocumento(s.getId(), TipoDocumento.CERTIFICADO, "a.pdf"));
    }

    @Teste("dentro do prazo (último dia) a solicitação continua aberta")
    public void ultimoDiaDoPrazo() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        relogio.avancarDias(7); // 23/11, último dia
        igual(0, gestor.atualizarPrazos());
        igual(StatusSolicitacao.AGUARDANDO_ANEXOS, s.getStatus());
    }

    @Teste("teto da modalidade: deferimento parcial e depois modalidade indisponível")
    public void tetoDaModalidade() {
        eventoDeferido(40);
        Solicitacao segunda = eventoDeferido(40); // só restam 20h dos 60h
        igual(20, segunda.getHorasAproveitadas());
        contem(segunda.getParecer(), "parcialmente");
        verdadeiro(!gestor.modalidadesDisponiveis().contains(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS),
                "modalidade completa não deve aparecer como disponível");
        lanca(RegraNegocioException.class, () -> gestor.criarSolicitacao(evento(10, 10)));
    }

    @Teste("publicações: 20h por trabalho")
    public void publicacoes() {
        Solicitacao s = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.PUBLICACOES)
                .descricao("Artigo").periodo(hoje.minusDays(20), hoje.minusDays(10))
                .cargaTotal(50).cargaSolicitada(50).quantidadeTrabalhos(1));
        gestor.anexarDocumento(s.getId(), TipoDocumento.COMPROVANTE, "aceite.pdf");
        gestor.enviarParaAnalise(s.getId());
        igual(20, gestor.analisar(s.getId()).getHorasAproveitadas());
    }

    @Teste("só pode haver uma solicitação em andamento por modalidade")
    public void umaPorModalidade() {
        gestor.criarSolicitacao(evento(20, 20));
        var e = lanca(RegraNegocioException.class, () -> gestor.criarSolicitacao(evento(10, 10)));
        contem(e.getMessage(), "em andamento");
    }

    @Teste("após indeferimento, o aluno pode abrir nova solicitação da mesma modalidade")
    public void refazerAposIndeferimento() {
        Solicitacao s = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.MONITORIA)
                .descricao("Monitoria").periodo(hoje.minusDays(40), hoje.minusDays(10))
                .cargaTotal(60).cargaSolicitada(60));
        gestor.anexarDocumento(s.getId(), TipoDocumento.RELATORIO_ALUNO, "rel.pdf"); // falta o parecer do orientador
        gestor.enviarParaAnalise(s.getId());
        igual(StatusSolicitacao.INDEFERIDA, gestor.analisar(s.getId()).getStatus());

        Solicitacao outra = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.MONITORIA)
                .descricao("Monitoria (refeita)").periodo(hoje.minusDays(40), hoje.minusDays(10))
                .cargaTotal(60).cargaSolicitada(60));
        igual(StatusSolicitacao.AGUARDANDO_ANEXOS, outra.getStatus());
    }

    @Teste("atividade com data final no futuro é recusada")
    public void dataFinalNoFuturo() {
        var dados = new SolicitacaoBuilder().modalidade(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS)
                .descricao("Evento futuro").periodo(hoje.plusDays(1), hoje.plusDays(2))
                .cargaTotal(10).cargaSolicitada(10);
        var e = lanca(RegraNegocioException.class, () -> gestor.criarSolicitacao(dados));
        contem(e.getMessage(), "futuro");
    }

    @Teste("resumo: regra das 2 modalidades e meta de 200h")
    public void resumoDuasModalidades() {
        eventoDeferido(20);
        ResumoProgresso um = gestor.resumo();
        igual(1L, um.modalidadesComHoras());
        verdadeiro(!um.cumpreMinimoModalidades(), "com uma modalidade ainda não cumpre o mínimo de 2");
        igual(200, um.horasExigidas());
        igual(180, um.horasFaltantes());

        monitoriaDeferida(90);
        ResumoProgresso dois = gestor.resumo();
        verdadeiro(dois.cumpreMinimoModalidades(), "com duas modalidades cumpre o mínimo");
        igual(110, dois.horasAproveitadas());
        verdadeiro(!dois.concluido(), "110h ainda não atinge as 200h");
    }

    @Teste("observer: cada mudança de estado vira um evento no histórico")
    public void historicoDeEventos() {
        eventoDeferido(20);
        igual(3, historico.linhas().size()); // criada, em análise, deferida
        contem(historico.linhas().get(2), "Deferida");
    }

    @Teste("análise só vale para solicitações em análise")
    public void analisarForaDeAnalise() {
        Solicitacao s = gestor.criarSolicitacao(evento(20, 20));
        lanca(RegraNegocioException.class, () -> gestor.analisar(s.getId()));
        lanca(RegraNegocioException.class, () -> gestor.buscar(999));
    }
}
