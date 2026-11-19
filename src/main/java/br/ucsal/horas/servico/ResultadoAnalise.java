package br.ucsal.horas.servico;

/** Resultado da análise do NDE: deferida (com horas) ou indeferida (com motivo no parecer). */
public record ResultadoAnalise(boolean deferida, int horas, String parecer) {
}
