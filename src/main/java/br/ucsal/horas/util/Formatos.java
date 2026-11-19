package br.ucsal.horas.util;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Formatos compartilhados. O modo STRICT rejeita datas inexistentes (ex.: 31/02). */
public final class Formatos {

    public static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formatos() {
    }
}
