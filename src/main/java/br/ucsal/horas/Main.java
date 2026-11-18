package br.ucsal.horas;

import br.ucsal.horas.demo.DadosDemonstracao;
import br.ucsal.horas.demo.DemoRoteiro;
import br.ucsal.horas.eventos.HistoricoEventos;
import br.ucsal.horas.eventos.NotificadorConsole;
import br.ucsal.horas.repositorio.RepositorioEmMemoria;
import br.ucsal.horas.servico.GestorHoras;
import br.ucsal.horas.ui.ConsoleApp;
import br.ucsal.horas.ui.EntradaScanner;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.List;

/**
 * Ponto de entrada.
 * <pre>
 *   (sem argumentos)  menu interativo com dados de demonstração
 *   --demo            executa o roteiro automático do fluxo principal
 *   --vazio           começa sem nenhuma solicitação
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        List<String> opcoes = List.of(args);

        HistoricoEventos historico = new HistoricoEventos();
        GestorHoras gestor = new GestorHoras(DadosDemonstracao.alunoDemo(), new RepositorioEmMemoria(),
                Clock.systemDefaultZone());
        gestor.adicionarOuvinte(historico);

        if (!opcoes.contains("--vazio")) {
            DadosDemonstracao.popular(gestor);
        }
        gestor.adicionarOuvinte(new NotificadorConsole(out)); // só avisa a partir daqui, sem poluir a carga inicial

        if (opcoes.contains("--demo")) {
            new DemoRoteiro(gestor, out).executar();
            return;
        }
        new ConsoleApp(gestor, historico, new EntradaScanner(System.in, out), out).executar();
    }
}
