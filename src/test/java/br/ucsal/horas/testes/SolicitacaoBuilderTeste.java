package br.ucsal.horas.testes;

import br.ucsal.horas.dominio.Anexo;
import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.dominio.TipoDocumento;
import br.ucsal.horas.excecao.RegraNegocioException;

import java.time.LocalDate;

import static br.ucsal.horas.testes.Verificacoes.contem;
import static br.ucsal.horas.testes.Verificacoes.lanca;

public class SolicitacaoBuilderTeste {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 17);

    private SolicitacaoBuilder valido() {
        return new SolicitacaoBuilder().modalidade(Modalidade.MONITORIA).descricao("Monitoria")
                .periodo(HOJE.minusDays(30), HOJE.minusDays(1)).cargaTotal(60).cargaSolicitada(60);
    }

    @Teste("carga a aproveitar maior que a carga total é rejeitada")
    public void solicitadaMaiorQueTotal() {
        var e = lanca(RegraNegocioException.class, () -> valido().cargaSolicitada(61).validar());
        contem(e.getMessage(), "não pode exceder");
    }

    @Teste("data final anterior à inicial é rejeitada")
    public void datasInvertidas() {
        lanca(RegraNegocioException.class, () -> valido().periodo(HOJE, HOJE.minusDays(2)).validar());
    }

    @Teste("descrição em branco é rejeitada")
    public void descricaoVazia() {
        lanca(RegraNegocioException.class, () -> valido().descricao("   ").validar());
    }

    @Teste("carga zero ou negativa é rejeitada")
    public void cargaInvalida() {
        lanca(RegraNegocioException.class, () -> valido().cargaTotal(0).validar());
        lanca(RegraNegocioException.class, () -> valido().cargaSolicitada(-5).validar());
    }

    @Teste("modalidade ausente é rejeitada")
    public void semModalidade() {
        lanca(RegraNegocioException.class, () -> valido().modalidade(null).validar());
    }

    @Teste("anexo só aceita PDF, JPG e PNG")
    public void extensoesDeAnexo() {
        new Anexo(TipoDocumento.CERTIFICADO, "ok.PDF");
        new Anexo(TipoDocumento.CERTIFICADO, "foto.jpeg");
        lanca(RegraNegocioException.class, () -> new Anexo(TipoDocumento.CERTIFICADO, "virus.exe"));
        lanca(RegraNegocioException.class, () -> new Anexo(TipoDocumento.CERTIFICADO, "semextensao"));
        lanca(RegraNegocioException.class, () -> new Anexo(TipoDocumento.CERTIFICADO, " "));
    }
}
