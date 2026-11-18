package br.ucsal.horas.testes;

import java.util.Objects;

/** Asserções mínimas (equivalentes às do JUnit) para não depender de download de bibliotecas. */
public final class Verificacoes {

    @FunctionalInterface
    public interface Acao {
        void executar() throws Exception;
    }

    private Verificacoes() {
    }

    public static void verdadeiro(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    public static void igual(Object esperado, Object atual) {
        if (!Objects.equals(esperado, atual)) {
            throw new AssertionError("esperado <" + esperado + "> mas foi <" + atual + ">");
        }
    }

    public static void contem(String texto, String trecho) {
        if (texto == null || !texto.contains(trecho)) {
            throw new AssertionError("esperava encontrar \"" + trecho + "\" em: " + texto);
        }
    }

    /** Verifica que a ação lança a exceção esperada e devolve a exceção para novas conferências. */
    public static <T extends Throwable> T lanca(Class<T> tipo, Acao acao) {
        try {
            acao.executar();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) {
                return tipo.cast(t);
            }
            throw new AssertionError("esperava " + tipo.getSimpleName() + " mas lançou " + t, t);
        }
        throw new AssertionError("esperava " + tipo.getSimpleName() + " mas nada foi lançado");
    }
}
