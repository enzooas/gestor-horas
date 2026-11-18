package br.ucsal.horas.testes;

import br.ucsal.horas.dominio.Aluno;
import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.eventos.HistoricoEventos;
import br.ucsal.horas.repositorio.RepositorioEmMemoria;
import br.ucsal.horas.servico.GestorHoras;
import br.ucsal.horas.ui.ConsoleApp;
import br.ucsal.horas.ui.EntradaRoteirizada;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static br.ucsal.horas.testes.Verificacoes.contem;
import static br.ucsal.horas.testes.Verificacoes.igual;

/** Testes de ponta a ponta pelo menu, com entrada roteirizada (ninguém digita nada). */
public class ConsoleAppTeste {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
    private final GestorHoras gestor = new GestorHoras(new Aluno("Aluno Teste", "T-1", "Curso Teste", 3200),
            new RepositorioEmMemoria(), new RelogioMutavel(LocalDate.of(2026, 11, 16)));

    private String rodar(String... entradas) {
        new ConsoleApp(gestor, new HistoricoEventos(), new EntradaRoteirizada(List.of(entradas)), out).executar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Teste("menu: opção 0 encerra e opção inválida não derruba o programa")
    public void sairEOpcaoInvalida() {
        String saida = rodar("99", "abc", "0");
        contem(saida, "Opção inválida");
        contem(saida, "Até logo!");
    }

    @Teste("menu: resumo e modalidades abrem sem erro")
    public void resumoEModalidades() {
        String saida = rodar("1", "2", "0");
        contem(saida, "Resumo de horas complementares");
        contem(saida, "Monitoria");
    }

    @Teste("menu: fluxo completo com entradas inválidas no meio (recupera e continua)")
    public void fluxoCompletoComErrosDeDigitacao() {
        String saida = rodar(
                "3", "s",                       // nova solicitação, aceite
                "4",                            // modalidade: Eventos acadêmico-científicos (4ª da lista)
                "Semana Acadêmica",             // descrição
                "31/02/2026", "05/11/2026",     // data inicial inválida (31/02) e depois válida
                "06/11/2026",                   // data final
                "vinte", "20",                  // carga total: texto inválido e depois válido
                "20",                           // carga a aproveitar
                "4", "1", "1", "certificado.pdf", // anexar: solicitação 1, documento 1, nome do arquivo
                "5", "1",                       // enviar para análise
                "7",                            // NDE julga
                "0");
        contem(saida, "Data inválida");
        contem(saida, "Digite um número inteiro");
        contem(saida, "Solicitação #1 criada");
        contem(saida, "Documento anexado");
        contem(saida, "enviada ao NDE");
        contem(saida, "Deferida integralmente: 20h");
        igual(StatusSolicitacao.DEFERIDA, gestor.buscar(1).getStatus());
    }

    @Teste("menu: arquivo inelegível mostra erro e o menu continua")
    public void arquivoInelegivelNoMenu() {
        String saida = rodar(
                "3", "s", "4", "Evento", "01/11/2026", "02/11/2026", "10", "10",
                "4", "1", "1", "virus.exe",
                "0");
        contem(saida, "Arquivo inelegível");
        contem(saida, "Até logo!");
    }

    @Teste("menu: recusar o aceite não cria solicitação")
    public void recusarAceite() {
        String saida = rodar("3", "n", "0");
        contem(saida, "Solicitação não iniciada");
        igual(0, gestor.listarSolicitacoes().size());
    }

    @Teste("menu: entrada encerrada no meio de um formulário termina com elegância")
    public void fimDaEntrada() {
        String saida = rodar("3", "s", "1");
        contem(saida, "entrada encerrada");
    }
}
