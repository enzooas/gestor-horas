package br.ucsal.horas.servico;

import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.TipoDocumento;

import java.util.stream.Collectors;

/**
 * Simula a análise do NDE: confere os documentos exigidos e aplica a regra da modalidade (Strategy).
 * Não existe nenhum "switch" por modalidade aqui: a política vem de {@code Modalidade#getRegra()}.
 */
public class AnalisadorNDE {

    public ResultadoAnalise analisar(Solicitacao solicitacao, int horasJaAproveitadasNaModalidade) {
        var faltantes = solicitacao.documentosFaltantes();
        if (!faltantes.isEmpty()) {
            String lista = faltantes.stream().map(TipoDocumento::getRotulo).collect(Collectors.joining(", "));
            return new ResultadoAnalise(false, 0, "Documentos obrigatórios ausentes: " + lista + ".");
        }

        int horas = solicitacao.getModalidade().getRegra().calcular(solicitacao, horasJaAproveitadasNaModalidade);
        if (horas <= 0) {
            return new ResultadoAnalise(false, 0, "O limite de "
                    + solicitacao.getModalidade().getLimiteHoras() + "h desta modalidade já foi atingido.");
        }
        if (horas < solicitacao.getCargaSolicitada()) {
            return new ResultadoAnalise(true, horas, "Deferida parcialmente: " + horas + "h de "
                    + solicitacao.getCargaSolicitada() + "h solicitadas ("
                    + solicitacao.getModalidade().getRegra().descricao() + ").");
        }
        return new ResultadoAnalise(true, horas, "Deferida integralmente: " + horas + "h.");
    }
}
