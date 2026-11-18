package br.ucsal.horas.eventos;

import java.io.PrintStream;

/** Observer que avisa o aluno, no console, de cada mudança de estado. */
public class NotificadorConsole implements OuvinteStatus {

    private final PrintStream saida;

    public NotificadorConsole(PrintStream saida) {
        this.saida = saida;
    }

    @Override
    public void aoMudarStatus(EventoStatus evento) {
        saida.println("   >> " + evento.mensagem());
    }
}
