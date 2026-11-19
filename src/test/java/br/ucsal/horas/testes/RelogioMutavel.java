package br.ucsal.horas.testes;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/** Relógio controlável: permite "avançar dias" para testar o prazo de 5 dias úteis sem esperar. */
public class RelogioMutavel extends Clock {

    private final ZoneId zona = ZoneId.of("America/Bahia");
    private Instant instante;

    public RelogioMutavel(LocalDate dataInicial) {
        this.instante = dataInicial.atTime(10, 0).atZone(zona).toInstant();
    }

    public void avancarDias(long dias) {
        instante = instante.plusSeconds(dias * 24 * 3600);
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId novaZona) {
        return this;
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
