package br.ucsal.horas.servico;

import br.ucsal.horas.dominio.Aluno;
import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.StatusSolicitacao;

import java.util.Map;

/** Situação consolidada do aluno: horas por modalidade, meta (5%-10% do curso) e regra das 2 modalidades. */
public record ResumoProgresso(Aluno aluno, int horasAproveitadas,
                              Map<Modalidade, Integer> porModalidade,
                              Map<StatusSolicitacao, Long> porStatus) {

    public int horasMinimas() {
        return aluno.horasMinimas();
    }

    public int horasMaximas() {
        return aluno.horasMaximas();
    }

    /** Horas que entram no histórico: o que passar de 10% vira atividade eletiva. */
    public int horasContabilizadas() {
        return Math.min(horasAproveitadas, horasMaximas());
    }

    public int horasExcedentes() {
        return Math.max(0, horasAproveitadas - horasMaximas());
    }

    public int horasFaltantes() {
        return Math.max(0, horasMinimas() - horasAproveitadas);
    }

    public long modalidadesComHoras() {
        return porModalidade.values().stream().filter(h -> h > 0).count();
    }

    public boolean cumpreMinimoModalidades() {
        return modalidadesComHoras() >= GestorHoras.MINIMO_MODALIDADES;
    }

    public boolean cumpreHoras() {
        return horasAproveitadas >= horasMinimas();
    }

    public boolean concluido() {
        return cumpreHoras() && cumpreMinimoModalidades();
    }

    /** Percentual da meta mínima, limitado a 100. */
    public int percentual() {
        return (int) Math.min(100, Math.round(100.0 * horasAproveitadas / horasMinimas()));
    }
}
