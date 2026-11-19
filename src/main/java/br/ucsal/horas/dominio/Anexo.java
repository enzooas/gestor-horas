package br.ucsal.horas.dominio;

import br.ucsal.horas.excecao.RegraNegocioException;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Documento anexado a uma solicitação. O MVP registra apenas o nome do arquivo
 * (não há upload real); a elegibilidade é verificada pela extensão.
 */
public record Anexo(TipoDocumento tipo, String nomeArquivo) {

    private static final Set<String> EXTENSOES_ACEITAS = Set.of("pdf", "jpg", "jpeg", "png");

    public Anexo {
        Objects.requireNonNull(tipo, "tipo");
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new RegraNegocioException("Informe o nome do arquivo digitalizado.");
        }
        nomeArquivo = nomeArquivo.trim();
        int ponto = nomeArquivo.lastIndexOf('.');
        String extensao = ponto < 0 ? "" : nomeArquivo.substring(ponto + 1).toLowerCase(Locale.ROOT);
        if (!EXTENSOES_ACEITAS.contains(extensao)) {
            throw new RegraNegocioException(
                    "Arquivo inelegível: \"" + nomeArquivo + "\". Envie a cópia digitalizada em PDF, JPG ou PNG.");
        }
    }
}
