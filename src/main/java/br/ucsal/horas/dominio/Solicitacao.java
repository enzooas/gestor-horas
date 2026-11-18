package br.ucsal.horas.dominio;

import br.ucsal.horas.excecao.RegraNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Solicitação de aproveitamento de uma atividade complementar (uma por modalidade).
 * Criada pelo {@link SolicitacaoBuilder}; o ciclo de vida é controlado por {@link StatusSolicitacao}.
 */
public class Solicitacao {

    private final int id;
    private final Modalidade modalidade;
    private final String descricao;
    private final LocalDate dataInicial;
    private final LocalDate dataFinal;
    private final int cargaTotal;
    private final int cargaSolicitada;
    private final int quantidadeTrabalhos;
    private final LocalDate criadaEm;
    private final LocalDate prazoAnexos;
    private final List<Anexo> anexos = new ArrayList<>();

    private StatusSolicitacao status = StatusSolicitacao.AGUARDANDO_ANEXOS;
    private int horasAproveitadas;
    private String parecer = "";

    Solicitacao(int id, Modalidade modalidade, String descricao, LocalDate dataInicial, LocalDate dataFinal,
                int cargaTotal, int cargaSolicitada, int quantidadeTrabalhos,
                LocalDate criadaEm, LocalDate prazoAnexos) {
        this.id = id;
        this.modalidade = modalidade;
        this.descricao = descricao;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.cargaTotal = cargaTotal;
        this.cargaSolicitada = cargaSolicitada;
        this.quantidadeTrabalhos = quantidadeTrabalhos;
        this.criadaEm = criadaEm;
        this.prazoAnexos = prazoAnexos;
    }

    public void anexar(Anexo anexo) {
        anexos.add(anexo);
    }

    /** Documentos exigidos pela modalidade que ainda não foram anexados. */
    public Set<TipoDocumento> documentosFaltantes() {
        Set<TipoDocumento> faltantes = EnumSet.copyOf(modalidade.getDocumentosExigidos());
        anexos.stream().map(Anexo::tipo).forEach(faltantes::remove);
        return faltantes;
    }

    public void transitarPara(StatusSolicitacao destino, String parecer) {
        if (!status.podeIrPara(destino)) {
            throw new RegraNegocioException("Transição inválida: " + status.getRotulo() + " -> " + destino.getRotulo());
        }
        this.status = destino;
        this.parecer = parecer == null ? "" : parecer;
    }

    public void definirHorasAproveitadas(int horas) {
        this.horasAproveitadas = horas;
    }

    public int getId() {
        return id;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public int getCargaTotal() {
        return cargaTotal;
    }

    public int getCargaSolicitada() {
        return cargaSolicitada;
    }

    public int getQuantidadeTrabalhos() {
        return quantidadeTrabalhos;
    }

    public LocalDate getCriadaEm() {
        return criadaEm;
    }

    public LocalDate getPrazoAnexos() {
        return prazoAnexos;
    }

    public List<Anexo> getAnexos() {
        return Collections.unmodifiableList(anexos);
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public int getHorasAproveitadas() {
        return horasAproveitadas;
    }

    public String getParecer() {
        return parecer;
    }
}
