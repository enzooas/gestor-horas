package br.ucsal.horas.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Entrada pré-definida: devolve as respostas em ordem. Útil em testes e para ensaiar a demonstração. */
public class EntradaRoteirizada implements Entrada {

    private final Deque<String> respostas;

    public EntradaRoteirizada(List<String> respostas) {
        this.respostas = new ArrayDeque<>(respostas);
    }

    @Override
    public String ler(String prompt) {
        if (respostas.isEmpty()) {
            throw new FimDaEntradaException();
        }
        return respostas.poll();
    }
}
