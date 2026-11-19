package br.ucsal.horas.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Entrada real, lida do teclado (ou de um pipe). */
public class EntradaScanner implements Entrada {

    private final Scanner scanner;
    private final PrintStream saida;

    public EntradaScanner(InputStream in, PrintStream saida) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.saida = saida;
    }

    @Override
    public String ler(String prompt) {
        saida.print(prompt);
        saida.flush();
        if (!scanner.hasNextLine()) {
            throw new FimDaEntradaException();
        }
        return scanner.nextLine();
    }
}
