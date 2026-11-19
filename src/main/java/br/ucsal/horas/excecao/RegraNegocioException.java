package br.ucsal.horas.excecao;

/**
 * Violação de uma regra de negócio das Atividades Complementares
 * (ex.: arquivo inelegível, prazo vencido, limite excedido).
 * A mensagem é sempre pensada para ser exibida ao aluno.
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
