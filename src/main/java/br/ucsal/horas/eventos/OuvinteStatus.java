package br.ucsal.horas.eventos;

/** Padrão Observer: quem quiser reagir a mudanças de estado implementa esta interface. */
@FunctionalInterface
public interface OuvinteStatus {

    void aoMudarStatus(EventoStatus evento);
}
