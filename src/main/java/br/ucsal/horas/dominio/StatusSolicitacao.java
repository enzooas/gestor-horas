package br.ucsal.horas.dominio;

/**
 * Estados de uma solicitação. As transições válidas ficam no próprio enum,
 * de modo que nenhuma outra classe precise conhecer o ciclo de vida.
 */
public enum StatusSolicitacao {
    AGUARDANDO_ANEXOS("Aguardando anexos"),
    EM_ANALISE("Em análise (NDE)"),
    DEFERIDA("Deferida"),
    INDEFERIDA("Indeferida"),
    CANCELADA("Cancelada");

    private final String rotulo;

    StatusSolicitacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public boolean podeIrPara(StatusSolicitacao destino) {
        return switch (this) {
            case AGUARDANDO_ANEXOS -> destino == EM_ANALISE || destino == CANCELADA;
            case EM_ANALISE -> destino == DEFERIDA || destino == INDEFERIDA;
            case DEFERIDA, INDEFERIDA, CANCELADA -> false;
        };
    }

    /** Solicitação ainda em andamento (ocupa a "vaga" da modalidade). */
    public boolean isAberta() {
        return this == AGUARDANDO_ANEXOS || this == EM_ANALISE;
    }
}
