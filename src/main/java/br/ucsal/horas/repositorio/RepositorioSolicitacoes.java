package br.ucsal.horas.repositorio;

import br.ucsal.horas.dominio.Solicitacao;

import java.util.List;
import java.util.Optional;

/** Abstração de persistência (Repository). O MVP usa a versão em memória; trocar por arquivo/BD não afeta o serviço. */
public interface RepositorioSolicitacoes {

    int proximoId();

    void salvar(Solicitacao solicitacao);

    Optional<Solicitacao> buscar(int id);

    /** Todas as solicitações, ordenadas por id. */
    List<Solicitacao> listar();
}
