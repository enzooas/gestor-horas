package br.ucsal.horas.testes;

import br.ucsal.horas.util.CalendarioUtil;

import java.time.LocalDate;

import static br.ucsal.horas.testes.Verificacoes.igual;

public class CalendarioUtilTeste {

    @Teste("5 dias úteis a partir de uma quarta-feira caem na quarta seguinte")
    public void cincoDiasUteisDaQuarta() {
        igual(LocalDate.of(2026, 11, 25), CalendarioUtil.somarDiasUteis(LocalDate.of(2026, 11, 18), 5));
    }

    @Teste("1 dia útil a partir de uma sexta-feira cai na segunda")
    public void sextaMaisUmVaiParaSegunda() {
        igual(LocalDate.of(2026, 11, 23), CalendarioUtil.somarDiasUteis(LocalDate.of(2026, 11, 20), 1));
    }

    @Teste("a partir de um sábado, 1 dia útil também cai na segunda")
    public void sabadoMaisUm() {
        igual(LocalDate.of(2026, 11, 23), CalendarioUtil.somarDiasUteis(LocalDate.of(2026, 11, 21), 1));
    }

    @Teste("0 dias úteis mantém a data")
    public void zeroDias() {
        igual(LocalDate.of(2026, 11, 17), CalendarioUtil.somarDiasUteis(LocalDate.of(2026, 11, 17), 0));
    }
}
