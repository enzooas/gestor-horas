package br.ucsal.horas.ui;

/** Fonte de entrada do console. Abstrair o teclado permite testar o menu sem digitar nada. */
public interface Entrada {

    /** Exibe o prompt e devolve a linha digitada; lança {@link FimDaEntradaException} se não houver mais entrada. */
    String ler(String prompt);
}
