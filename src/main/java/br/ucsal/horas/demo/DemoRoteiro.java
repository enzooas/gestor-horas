package br.ucsal.horas.demo;

import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.dominio.TipoDocumento;
import br.ucsal.horas.excecao.RegraNegocioException;
import br.ucsal.horas.servico.GestorHoras;
import br.ucsal.horas.ui.ApresentacaoResumo;

import java.io.PrintStream;
import java.time.LocalDate;

/**
 * Roteiro automático do fluxo principal (executar com {@code --demo}). Serve para ensaiar o pitch,
 * gravar o vídeo de contingência e conferir rapidamente que tudo funciona.
 */
public class DemoRoteiro {

    private final GestorHoras gestor;
    private final PrintStream out;

    public DemoRoteiro(GestorHoras gestor, PrintStream out) {
        this.gestor = gestor;
        this.out = out;
    }

    public void executar() {
        LocalDate hoje = gestor.hoje();

        passo("1. Situação inicial do aluno");
        ApresentacaoResumo.imprimir(out, gestor.resumo());

        passo("2. Aluno cria uma solicitação de voluntariado");
        Solicitacao vol = gestor.criarSolicitacao(new SolicitacaoBuilder()
                .modalidade(Modalidade.VOLUNTARIADO)
                .descricao("Ação social em escola pública (fictícia)")
                .periodo(hoje.minusDays(12), hoje.minusDays(12))
                .cargaTotal(30).cargaSolicitada(30));

        passo("3. Tentativa de anexar arquivo inelegível (.exe)");
        try {
            gestor.anexarDocumento(vol.getId(), TipoDocumento.CERTIFICADO, "certificado.exe");
        } catch (RegraNegocioException e) {
            out.println("   [!] " + e.getMessage());
        }

        passo("4. Envio incompleto (falta o relatório do aluno) -> NDE indefere");
        gestor.anexarDocumento(vol.getId(), TipoDocumento.CERTIFICADO, "certificado_acao_social.pdf");
        var faltantes = gestor.enviarParaAnalise(vol.getId());
        out.println("   Faltando: " + faltantes.stream().map(TipoDocumento::getRotulo).toList());
        out.println("   Parecer do NDE: " + gestor.analisar(vol.getId()).getParecer());

        passo("5. Aluno refaz a solicitação, agora completa -> deferida");
        Solicitacao nova = gestor.criarSolicitacao(new SolicitacaoBuilder()
                .modalidade(Modalidade.VOLUNTARIADO)
                .descricao("Ação social em escola pública (fictícia) - completa")
                .periodo(hoje.minusDays(12), hoje.minusDays(12))
                .cargaTotal(30).cargaSolicitada(30));
        gestor.anexarDocumento(nova.getId(), TipoDocumento.CERTIFICADO, "certificado_acao_social.pdf");
        gestor.anexarDocumento(nova.getId(), TipoDocumento.RELATORIO_ALUNO, "relatorio_acao_social.pdf");
        gestor.enviarParaAnalise(nova.getId());

        passo("6. Aluno completa a solicitação de extensão que estava pendente");
        gestor.listarPorStatus(StatusSolicitacao.AGUARDANDO_ANEXOS).forEach(s -> {
            for (TipoDocumento tipo : s.documentosFaltantes()) {
                gestor.anexarDocumento(s.getId(), tipo, "relatorio_minicurso.pdf");
            }
            gestor.enviarParaAnalise(s.getId());
        });

        passo("7. NDE julga tudo que está em análise");
        gestor.analisarPendentes().forEach(s ->
                out.printf("   #%d (%s): %s - %s%n", s.getId(), s.getModalidade().getRotulo(),
                        s.getStatus().getRotulo(), s.getParecer()));

        passo("8. Situação final");
        ApresentacaoResumo.imprimir(out, gestor.resumo());
    }

    private void passo(String titulo) {
        out.println();
        out.println("=== " + titulo + " ===");
    }
}
