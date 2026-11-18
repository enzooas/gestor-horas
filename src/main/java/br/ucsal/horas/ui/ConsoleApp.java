package br.ucsal.horas.ui;

import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.dominio.TipoDocumento;
import br.ucsal.horas.eventos.HistoricoEventos;
import br.ucsal.horas.excecao.RegraNegocioException;
import br.ucsal.horas.servico.GestorHoras;
import br.ucsal.horas.util.Formatos;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Menu interativo de console. Cada opção é uma ação registrada em um mapa (estilo Command),
 * então incluir uma opção nova é uma linha em {@link #registrarComandos()}.
 * Entradas inválidas nunca derrubam o programa: o erro é mostrado e o menu volta.
 */
public class ConsoleApp {

    private record ItemMenu(String titulo, Runnable acao) {
    }

    private final GestorHoras gestor;
    private final HistoricoEventos historico;
    private final Entrada entrada;
    private final PrintStream out;
    private final Map<String, ItemMenu> menu = new LinkedHashMap<>();

    public ConsoleApp(GestorHoras gestor, HistoricoEventos historico, Entrada entrada, PrintStream out) {
        this.gestor = gestor;
        this.historico = historico;
        this.entrada = entrada;
        this.out = out;
        registrarComandos();
    }

    private void registrarComandos() {
        menu.put("1", new ItemMenu("Ver resumo do meu progresso", this::mostrarResumo));
        menu.put("2", new ItemMenu("Ver modalidades, limites e documentos exigidos", this::mostrarModalidades));
        menu.put("3", new ItemMenu("Nova solicitação de aproveitamento", this::novaSolicitacao));
        menu.put("4", new ItemMenu("Anexar documento a uma solicitação", this::anexarDocumento));
        menu.put("5", new ItemMenu("Enviar solicitação para análise do NDE", this::enviarParaAnalise));
        menu.put("6", new ItemMenu("Listar minhas solicitações", this::listarSolicitacoes));
        menu.put("7", new ItemMenu("Simular análise do NDE (julgar solicitações em análise)", this::simularNde));
        menu.put("8", new ItemMenu("Ver histórico de eventos", this::mostrarHistorico));
    }

    public void executar() {
        out.println("=== Gestor de Horas Complementares (UCSal) ===");
        out.println("Dados de demonstração fictícios. Nenhum arquivo real é enviado: informe apenas o nome do arquivo.");
        try {
            while (true) {
                imprimirMenu();
                String opcao = entrada.ler("Opção: ").trim();
                if (opcao.equals("0")) {
                    break;
                }
                ItemMenu item = menu.get(opcao);
                if (item == null) {
                    out.println("[!] Opção inválida. Digite um número do menu.");
                    continue;
                }
                try {
                    item.acao().run();
                } catch (RegraNegocioException e) {
                    out.println("[!] " + e.getMessage());
                }
            }
            out.println("Até logo!");
        } catch (FimDaEntradaException e) {
            out.println();
            out.println("(entrada encerrada)");
        }
    }

    private void imprimirMenu() {
        out.println();
        out.println("----- MENU -----");
        menu.forEach((chave, item) -> out.println(chave + ") " + item.titulo()));
        out.println("0) Sair");
    }

    // ---------------------------------------------------------------- ações

    private void mostrarResumo() {
        ApresentacaoResumo.imprimir(out, gestor.resumo());
    }

    private void mostrarModalidades() {
        List<Modalidade> disponiveis = gestor.modalidadesDisponiveis();
        out.println();
        out.println("Modalidades (limite do Ato nº 147/2014):");
        for (Modalidade m : Modalidade.values()) {
            String docs = m.getDocumentosExigidos().stream().map(TipoDocumento::getRotulo)
                    .collect(Collectors.joining("; "));
            out.printf("- %s | %s | já aproveitado: %dh | %s%n", m.getRotulo(), m.getRegra().descricao(),
                    gestor.horasAproveitadas(m), disponiveis.contains(m) ? "disponível" : "LIMITE ATINGIDO");
            out.println("    Documentos: " + docs);
        }
    }

    private void novaSolicitacao() {
        out.println("Aceite: declaro ciência de que devo registrar horas em pelo menos 2 modalidades e que tenho "
                + GestorHoras.PRAZO_ANEXOS_DIAS_UTEIS + " dias úteis para anexar os documentos, "
                + "caso contrário a solicitação será cancelada.");
        String aceite = entrada.ler("Você aceita? (s/n): ").trim().toLowerCase();
        if (!aceite.equals("s") && !aceite.equals("sim")) {
            out.println("Solicitação não iniciada.");
            return;
        }

        Modalidade modalidade = escolher("Modalidades disponíveis:", gestor.modalidadesDisponiveis(),
                m -> m.getRotulo() + " - " + m.getRegra().descricao());

        SolicitacaoBuilder dados = new SolicitacaoBuilder().modalidade(modalidade);
        dados.descricao(entrada.ler("Descrição da atividade: "));
        LocalDate inicio = lerData("Data inicial");
        LocalDate fim = lerData("Data final");
        dados.periodo(inicio, fim);
        dados.cargaTotal(lerInteiro("Carga horária total da atividade (h): "));
        dados.cargaSolicitada(lerInteiro("Carga horária que deseja aproveitar (h): "));
        if (modalidade == Modalidade.PUBLICACOES) {
            dados.quantidadeTrabalhos(lerInteiro("Quantidade de trabalhos publicados: "));
        }

        Solicitacao s = gestor.criarSolicitacao(dados);
        String exigidos = s.documentosFaltantes().stream().map(TipoDocumento::getRotulo)
                .collect(Collectors.joining(", "));
        out.printf("Solicitação #%d criada. Anexe até %s: %s.%n",
                s.getId(), Formatos.DATA.format(s.getPrazoAnexos()), exigidos);
    }

    private void anexarDocumento() {
        Solicitacao s = escolherSolicitacao(StatusSolicitacao.AGUARDANDO_ANEXOS);
        List<TipoDocumento> faltantes = List.copyOf(s.documentosFaltantes());
        if (faltantes.isEmpty()) {
            out.println("Todos os documentos exigidos já foram anexados. Envie para análise (opção 5).");
            return;
        }
        TipoDocumento tipo = escolher("Documentos que faltam:", faltantes, TipoDocumento::getRotulo);
        String arquivo = entrada.ler("Nome do arquivo digitalizado (ex.: certificado.pdf): ");
        gestor.anexarDocumento(s.getId(), tipo, arquivo);
        Set<TipoDocumento> restantes = s.documentosFaltantes();
        out.println("Documento anexado. " + (restantes.isEmpty()
                ? "Todos os documentos exigidos foram anexados."
                : "Ainda faltam " + restantes.size() + " documento(s)."));
    }

    private void enviarParaAnalise() {
        Solicitacao s = escolherSolicitacao(StatusSolicitacao.AGUARDANDO_ANEXOS);
        Set<TipoDocumento> faltantes = gestor.enviarParaAnalise(s.getId());
        out.println("Solicitação #" + s.getId() + " enviada ao NDE.");
        if (!faltantes.isEmpty()) {
            out.println("[!] Atenção: faltam documentos obrigatórios ("
                    + faltantes.stream().map(TipoDocumento::getRotulo).collect(Collectors.joining(", "))
                    + "). A solicitação pode ser indeferida.");
        }
    }

    private void listarSolicitacoes() {
        List<Solicitacao> todas = gestor.listarSolicitacoes();
        if (todas.isEmpty()) {
            out.println("Nenhuma solicitação registrada.");
            return;
        }
        out.println();
        for (Solicitacao s : todas) {
            out.printf("#%d | %s | %s | pedido: %dh | aproveitado: %dh | %s%n", s.getId(), s.getModalidade().getRotulo(),
                    s.getStatus().getRotulo(), s.getCargaSolicitada(), s.getHorasAproveitadas(), s.getDescricao());
            if (!s.getParecer().isBlank()) {
                out.println("     Parecer: " + s.getParecer());
            }
        }
    }

    private void simularNde() {
        List<Solicitacao> julgadas = gestor.analisarPendentes();
        if (julgadas.isEmpty()) {
            out.println("Não há solicitações em análise.");
            return;
        }
        for (Solicitacao s : julgadas) {
            out.printf("#%d -> %s. %s%n", s.getId(), s.getStatus().getRotulo(), s.getParecer());
        }
    }

    private void mostrarHistorico() {
        if (historico.linhas().isEmpty()) {
            out.println("Sem eventos registrados.");
        } else {
            historico.linhas().forEach(out::println);
        }
    }

    // ---------------------------------------------------------------- leitura segura

    private Solicitacao escolherSolicitacao(StatusSolicitacao status) {
        List<Solicitacao> candidatas = gestor.listarPorStatus(status);
        if (candidatas.isEmpty()) {
            throw new RegraNegocioException("Não há solicitações com situação \"" + status.getRotulo() + "\".");
        }
        return escolher("Solicitações (" + status.getRotulo() + "):", candidatas,
                s -> "#" + s.getId() + " " + s.getModalidade().getRotulo() + " - " + s.getDescricao());
    }

    private <T> T escolher(String titulo, List<T> opcoes, Function<T, String> rotulo) {
        if (opcoes.isEmpty()) {
            throw new RegraNegocioException("Nenhuma opção disponível.");
        }
        out.println(titulo);
        for (int i = 0; i < opcoes.size(); i++) {
            out.printf("  %2d) %s%n", i + 1, rotulo.apply(opcoes.get(i)));
        }
        while (true) {
            int n = lerInteiro("Número da opção: ");
            if (n >= 1 && n <= opcoes.size()) {
                return opcoes.get(n - 1);
            }
            out.println("[!] Escolha um número entre 1 e " + opcoes.size() + ".");
        }
    }

    private int lerInteiro(String prompt) {
        while (true) {
            String texto = entrada.ler(prompt).trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                out.println("[!] Digite um número inteiro (ex.: 20).");
            }
        }
    }

    private LocalDate lerData(String rotulo) {
        while (true) {
            String texto = entrada.ler(rotulo + " (dd/mm/aaaa): ").trim();
            try {
                return LocalDate.parse(texto, Formatos.DATA);
            } catch (DateTimeParseException e) {
                out.println("[!] Data inválida. Use o formato dd/mm/aaaa, com uma data real.");
            }
        }
    }
}
