package br.ucsal.horas.testes;

import br.ucsal.horas.dominio.Aluno;
import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.regras.LimitePorTrabalho;
import br.ucsal.horas.regras.LimiteTotal;

import java.time.LocalDate;

import static br.ucsal.horas.testes.Verificacoes.igual;

public class RegrasAproveitamentoTeste {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 17);

    private Solicitacao solicitacao(Modalidade modalidade, int total, int pedido, int trabalhos) {
        return new SolicitacaoBuilder().modalidade(modalidade).descricao("Teste")
                .periodo(HOJE.minusDays(10), HOJE.minusDays(5))
                .cargaTotal(total).cargaSolicitada(pedido).quantidadeTrabalhos(trabalhos)
                .build(1, HOJE, HOJE.plusDays(7));
    }

    @Teste("LimiteTotal aproveita tudo o que foi pedido quando cabe no teto")
    public void cabeNoTeto() {
        igual(20, new LimiteTotal(60).calcular(solicitacao(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS, 20, 20, 1), 0));
    }

    @Teste("LimiteTotal corta o que excede o teto restante da modalidade")
    public void cortaNoTeto() {
        igual(20, new LimiteTotal(60).calcular(solicitacao(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS, 40, 40, 1), 40));
    }

    @Teste("LimiteTotal devolve zero quando o teto já foi atingido")
    public void tetoEsgotado() {
        igual(0, new LimiteTotal(60).calcular(solicitacao(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS, 10, 10, 1), 60));
    }

    @Teste("Publicações: no máximo 20h por trabalho")
    public void publicacaoPorTrabalho() {
        var regra = new LimitePorTrabalho(60, 20);
        igual(20, regra.calcular(solicitacao(Modalidade.PUBLICACOES, 50, 50, 1), 0));
        igual(40, regra.calcular(solicitacao(Modalidade.PUBLICACOES, 50, 50, 2), 0));
    }

    @Teste("Publicações: o teto de 60h continua valendo mesmo com muitos trabalhos")
    public void publicacaoRespeitaTeto() {
        igual(60, new LimitePorTrabalho(60, 20).calcular(solicitacao(Modalidade.PUBLICACOES, 100, 100, 5), 0));
    }

    @Teste("Cada modalidade expõe o limite do Ato nº 147/2014")
    public void limitesDasModalidades() {
        igual(120, Modalidade.EXTENSAO.getLimiteHoras());
        igual(90, Modalidade.MONITORIA.getLimiteHoras());
        igual(40, Modalidade.CURSOS_ATUALIZACAO.getLimiteHoras());
        igual(20, Modalidade.BANCAS.getLimiteHoras());
        igual(15, Modalidade.values().length);
    }

    @Teste("Meta do aluno: 5% mínimo e 10% máximo da carga do curso")
    public void metaDoAluno() {
        Aluno aluno = new Aluno("Teste", "T-1", "Curso", 3200);
        igual(160, aluno.horasMinimas());
        igual(320, aluno.horasMaximas());
    }
}
