package br.ucsal.horas.eventos;

import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.StatusSolicitacao;
import br.ucsal.horas.util.Formatos;

import java.time.LocalDate;

/** Evento publicado a cada mudança de estado de uma solicitação. {@code anterior} é nulo na criação. */
public record EventoStatus(int idSolicitacao, Modalidade modalidade, StatusSolicitacao anterior,
                           StatusSolicitacao novo, LocalDate data, String detalhe) {

    public String mensagem() {
        String origem = anterior == null ? "criada" : anterior.getRotulo() + " -> " + novo.getRotulo();
        String complemento = (detalhe == null || detalhe.isBlank()) ? "" : " | " + detalhe;
        return "[" + Formatos.DATA.format(data) + "] Solicitação #" + idSolicitacao
                + " (" + modalidade.getRotulo() + "): " + origem + complemento;
    }
}
