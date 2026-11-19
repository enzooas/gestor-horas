package br.ucsal.horas.regras;

import br.ucsal.horas.dominio.Solicitacao;

/**
 * Publicações: teto da modalidade (60h) e, além disso, no máximo 20h por trabalho.
 * Reaproveita {@link LimiteTotal} por composição.
 */
public record LimitePorTrabalho(int limite, int horasPorTrabalho) implements RegraAproveitamento {

    @Override
    public int limiteTotal() {
        return limite;
    }

    @Override
    public int calcular(Solicitacao solicitacao, int horasJaAproveitadas) {
        int base = new LimiteTotal(limite).calcular(solicitacao, horasJaAproveitadas);
        return Math.min(base, horasPorTrabalho * solicitacao.getQuantidadeTrabalhos());
    }

    @Override
    public String descricao() {
        return "até " + limite + "h (" + horasPorTrabalho + "h por trabalho)";
    }
}
