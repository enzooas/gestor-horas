package br.ucsal.horas.ui;

/** Sinaliza que a entrada acabou (Ctrl+D / Ctrl+Z ou roteiro de testes esgotado). */
public class FimDaEntradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FimDaEntradaException() {
        super("Fim da entrada");
    }
}
