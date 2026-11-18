package br.ucsal.horas.repositorio;

import br.ucsal.horas.dominio.Solicitacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RepositorioEmMemoria implements RepositorioSolicitacoes {

    private final TreeMap<Integer, Solicitacao> dados = new TreeMap<>();
    private final AtomicInteger sequencia = new AtomicInteger();

    @Override
    public int proximoId() {
        return sequencia.incrementAndGet();
    }

    @Override
    public void salvar(Solicitacao solicitacao) {
        dados.put(solicitacao.getId(), solicitacao);
    }

    @Override
    public Optional<Solicitacao> buscar(int id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Solicitacao> listar() {
        return new ArrayList<>(dados.values());
    }
}
