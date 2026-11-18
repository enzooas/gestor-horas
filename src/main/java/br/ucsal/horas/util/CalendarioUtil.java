package br.ucsal.horas.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Cálculo de dias úteis (sábados e domingos). Feriados não são considerados no MVP. */
public final class CalendarioUtil {

    private CalendarioUtil() {
    }

    public static LocalDate somarDiasUteis(LocalDate inicio, int diasUteis) {
        if (diasUteis < 0) {
            throw new IllegalArgumentException("diasUteis deve ser >= 0");
        }
        LocalDate data = inicio;
        int restantes = diasUteis;
        while (restantes > 0) {
            data = data.plusDays(1);
            if (!fimDeSemana(data)) {
                restantes--;
            }
        }
        return data;
    }

    private static boolean fimDeSemana(LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }
}
