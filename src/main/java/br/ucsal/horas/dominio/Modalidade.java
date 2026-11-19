package br.ucsal.horas.dominio;

import br.ucsal.horas.regras.LimitePorTrabalho;
import br.ucsal.horas.regras.LimiteTotal;
import br.ucsal.horas.regras.RegraAproveitamento;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static br.ucsal.horas.dominio.TipoDocumento.*;

/**
 * As 15 modalidades do Ato nº 147/2014 (matriz de convalidação), com o teto de horas
 * e os documentos exigidos por modalidade (Resolução CONSUN nº 14/2016, art. 4º).
 */
public enum Modalidade {
    DISCIPLINAS_EXTRACURRICULARES("Disciplinas extracurriculares", new LimiteTotal(120), COMPROVANTE),
    PESQUISA("Atividades de pesquisa", new LimiteTotal(120), RELATORIO_ALUNO, RELATORIO_ORIENTADOR),
    EXTENSAO("Atividades de extensão", new LimiteTotal(120), RELATORIO_ALUNO, CERTIFICADO),
    EVENTOS_ACADEMICO_CIENTIFICOS("Eventos acadêmico-científicos", new LimiteTotal(60), CERTIFICADO),
    APRESENTACAO_TRABALHO("Apresentação de trabalho científico", new LimiteTotal(60), CERTIFICADO),
    PUBLICACOES("Publicações, registro e aceitação de trabalhos", new LimitePorTrabalho(60, 20), COMPROVANTE),
    VOLUNTARIADO("Atividades sociais de voluntariado e cidadania", new LimiteTotal(60), RELATORIO_ALUNO, CERTIFICADO),
    MONITORIA("Monitoria", new LimiteTotal(90), RELATORIO_ALUNO, RELATORIO_ORIENTADOR),
    ESTAGIOS("Estágios extracurriculares", new LimiteTotal(90), TERMO_ESTAGIO, RELATORIO_ALUNO, RELATORIO_ORIENTADOR),
    EMPRESA_JUNIOR("Empresa júnior, escritório modelo ou similares", new LimiteTotal(60), ATESTADO),
    CURSOS_ATUALIZACAO("Programas/cursos de atualização profissional", new LimiteTotal(40), CERTIFICADO),
    REPRESENTACAO_DISCENTE("Representação discente", new LimiteTotal(20), ATESTADO),
    EVENTOS_REPRESENTACAO("Eventos de órgãos de representação estudantil", new LimiteTotal(20), CERTIFICADO),
    INTERCAMBIO("Intercâmbio nacional ou internacional", new LimiteTotal(120), COMPROVANTE),
    BANCAS("Ouvinte em bancas (TCC, mestrado, doutorado)", new LimiteTotal(20), CERTIFICADO);

    private final String rotulo;
    private final RegraAproveitamento regra;
    private final Set<TipoDocumento> documentosExigidos;

    Modalidade(String rotulo, RegraAproveitamento regra, TipoDocumento... documentos) {
        this.rotulo = rotulo;
        this.regra = regra;
        this.documentosExigidos = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(documentos)));
    }

    public String getRotulo() {
        return rotulo;
    }

    public RegraAproveitamento getRegra() {
        return regra;
    }

    public int getLimiteHoras() {
        return regra.limiteTotal();
    }

    public Set<TipoDocumento> getDocumentosExigidos() {
        return documentosExigidos;
    }
}
