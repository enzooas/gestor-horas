package br.ucsal.horas.dominio;

/** Tipos de documento comprobatório exigidos pela Resolução CONSUN nº 14/2016 e pelo Ato nº 147/2014. */
public enum TipoDocumento {
    CERTIFICADO("Certificado de participação"),
    RELATORIO_ALUNO("Relatório do aluno"),
    RELATORIO_ORIENTADOR("Relatório/parecer do professor orientador"),
    TERMO_ESTAGIO("Termo de compromisso de estágio"),
    ATESTADO("Atestado"),
    COMPROVANTE("Documentação comprobatória");

    private final String rotulo;

    TipoDocumento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
