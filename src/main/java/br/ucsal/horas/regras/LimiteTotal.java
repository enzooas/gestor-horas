package br.ucsal.horas.regras;

import br.ucsal.horas.dominio.Solicitacao;

/** Regra padrão: aproveita o menor valor entre o solicitado, a carga real e o que resta do teto. */
public record LimiteTotal(int limite) implements RegraAproveitamento {

    @Override
    public int limiteTotal() {
        return limite;
    }

    @Override
    public int calcular(Solicitacao solicitacao, int horasJaAproveitadas) {
        int restante = Math.max(0, limite - horasJaAproveitadas);
        int pedido = Math.min(solicitacao.getCargaSolicitada(), solicitacao.getCargaTotal());
        return Math.min(pedido, restante);
    }

    @Override
    public String descricao() {
        return "até " + limite + "h";
    }
}
