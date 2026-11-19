package br.ucsal.horas.ui;

import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.servico.ResumoProgresso;

import java.io.PrintStream;

/** Formata o resumo de progresso. Estado sempre em texto (não depende só de cor ou símbolo). */
public final class ApresentacaoResumo {

    private static final int LARGURA_BARRA = 24;

    private ApresentacaoResumo() {
    }

    public static void imprimir(PrintStream out, ResumoProgresso r) {
        out.println();
        out.println("--- Resumo de horas complementares ---");
        out.printf("Aluno: %s (matrícula %s) | Curso: %s (%dh)%n",
                r.aluno().nome(), r.aluno().matricula(), r.aluno().curso(), r.aluno().cargaHorariaCurso());
        out.printf("Horas aproveitadas: %dh | Meta do curso: %dh (%.2f%% da carga) | Máximo contabilizado: %dh (10%%)%n",
                r.horasAproveitadas(), r.horasExigidas(), r.aluno().percentualDaCarga(), r.horasMaximas());
        out.println("Progresso da meta: " + barra(r.percentual()) + " " + r.percentual() + "%");

        out.printf("Modalidades com horas: %d (mínimo exigido: %d) -> %s%n",
                r.modalidadesComHoras(), 2, r.cumpreMinimoModalidades() ? "ATENDIDO" : "PENDENTE");

        if (r.porModalidade().isEmpty()) {
            out.println("Nenhuma hora deferida ainda.");
        } else {
            out.println("Horas deferidas por modalidade:");
            r.porModalidade().forEach((m, h) ->
                    out.printf("  - %-50s %3dh de %3dh%n", m.getRotulo(), h, m.getLimiteHoras()));
        }

        StringBuilder status = new StringBuilder();
        for (StatusSolicitacao s : StatusSolicitacao.values()) {
            long qtd = r.porStatus().getOrDefault(s, 0L);
            if (qtd > 0) {
                status.append(s.getRotulo()).append(": ").append(qtd).append("  ");
            }
        }
        out.println("Solicitações: " + (status.length() == 0 ? "nenhuma" : status.toString().trim()));

        if (r.horasExcedentes() > 0) {
            out.printf("Atenção: %dh excedem o máximo de 10%% e viram atividades eletivas.%n", r.horasExcedentes());
        }
        if (r.concluido()) {
            out.println("SITUAÇÃO: META CUMPRIDA (horas e modalidades).");
        } else {
            StringBuilder falta = new StringBuilder("SITUAÇÃO: em andamento.");
            if (!r.cumpreHoras()) {
                falta.append(" Faltam ").append(r.horasFaltantes()).append("h.");
            }
            if (!r.cumpreMinimoModalidades()) {
                falta.append(" Registre horas em pelo menos 2 modalidades diferentes.");
            }
            out.println(falta);
        }
        out.println();
    }

    private static String barra(int percentual) {
        int cheios = Math.round(LARGURA_BARRA * percentual / 100f);
        return "[" + "#".repeat(cheios) + "-".repeat(LARGURA_BARRA - cheios) + "]";
    }
}
