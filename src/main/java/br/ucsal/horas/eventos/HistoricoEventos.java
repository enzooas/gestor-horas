package br.ucsal.horas.eventos;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Observer que guarda o histórico de eventos para consulta no menu. */
public class HistoricoEventos implements OuvinteStatus {

    private final List<String> linhas = new CopyOnWriteArrayList<>();

    @Override
    public void aoMudarStatus(EventoStatus evento) {
        linhas.add(evento.mensagem());
    }

    public List<String> linhas() {
        return Collections.unmodifiableList(linhas);
    }
}
