package br.ucsal.horas.regras;

import br.ucsal.horas.dominio.Solicitacao;

/**
 * Padrão Strategy: cada modalidade carrega a sua própria política de cálculo
 * das horas aproveitáveis, sem que o analisador precise de "if/switch" por modalidade.
 */
public interface RegraAproveitamento {

    /** Teto de horas da modalidade (Ato nº 147/2014). */
    int limiteTotal();

    /** Horas que podem ser aproveitadas nesta solicitação, dado o que já foi aproveitado na modalidade. */
    int calcular(Solicitacao solicitacao, int horasJaAproveitadas);

    /** Texto curto para exibir ao aluno. */
    String descricao();
}
