package br.ucsal.horas.dominio;

import br.ucsal.horas.excecao.RegraNegocioException;

import java.time.LocalDate;

/**
 * Padrão Builder: monta a {@link Solicitacao} passo a passo (como o formulário do portal do aluno)
 * e concentra as validações de preenchimento em um único lugar.
 */
public class SolicitacaoBuilder {

    private Modalidade modalidade;
    private String descricao;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private int cargaTotal;
    private int cargaSolicitada;
    private int quantidadeTrabalhos = 1;

    public SolicitacaoBuilder modalidade(Modalidade modalidade) {
        this.modalidade = modalidade;
        return this;
    }

    public SolicitacaoBuilder descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public SolicitacaoBuilder periodo(LocalDate dataInicial, LocalDate dataFinal) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        return this;
    }

    public SolicitacaoBuilder cargaTotal(int horas) {
        this.cargaTotal = horas;
        return this;
    }

    public SolicitacaoBuilder cargaSolicitada(int horas) {
        this.cargaSolicitada = horas;
        return this;
    }

    /** Usado apenas em publicações (20h por trabalho). */
    public SolicitacaoBuilder quantidadeTrabalhos(int quantidade) {
        this.quantidadeTrabalhos = quantidade;
        return this;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void validar() {
        if (modalidade == null) {
            throw new RegraNegocioException("Selecione a modalidade da atividade.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new RegraNegocioException("Informe a descrição da atividade.");
        }
        if (dataInicial == null || dataFinal == null) {
            throw new RegraNegocioException("Informe a data inicial e a data final da atividade.");
        }
        if (dataFinal.isBefore(dataInicial)) {
            throw new RegraNegocioException("A data final não pode ser anterior à data inicial.");
        }
        if (cargaTotal <= 0) {
            throw new RegraNegocioException("A carga horária total deve ser maior que zero.");
        }
        if (cargaSolicitada <= 0) {
            throw new RegraNegocioException("A carga horária a aproveitar deve ser maior que zero.");
        }
        if (cargaSolicitada > cargaTotal) {
            throw new RegraNegocioException("A carga horária a aproveitar (" + cargaSolicitada
                    + "h) não pode exceder a carga total da atividade (" + cargaTotal + "h).");
        }
        if (quantidadeTrabalhos < 1) {
            throw new RegraNegocioException("A quantidade de trabalhos deve ser pelo menos 1.");
        }
    }

    public Solicitacao build(int id, LocalDate criadaEm, LocalDate prazoAnexos) {
        validar();
        return new Solicitacao(id, modalidade, descricao.trim(), dataInicial, dataFinal,
                cargaTotal, cargaSolicitada, quantidadeTrabalhos, criadaEm, prazoAnexos);
    }
}
