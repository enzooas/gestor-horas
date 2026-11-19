package br.ucsal.horas.testes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Executor de testes por reflexão: instancia cada classe, roda os métodos anotados com {@link Teste}
 * (uma instância nova por teste, para isolar o estado) e devolve código de saída 1 se algo falhar.
 */
public final class ExecutorTestes {

    private static final List<Class<?>> CLASSES = List.of(
            CalendarioUtilTeste.class,
            RegrasAproveitamentoTeste.class,
            SolicitacaoBuilderTeste.class,
            GestorHorasTeste.class,
            ConsoleAppTeste.class);

    private ExecutorTestes() {
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        int ok = 0;
        int falhas = 0;

        for (Class<?> classe : CLASSES) {
            System.out.println("\n" + classe.getSimpleName());
            Method[] metodos = Arrays.stream(classe.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Teste.class))
                    .sorted(Comparator.comparing(Method::getName))
                    .toArray(Method[]::new);

            for (Method metodo : metodos) {
                String descricao = metodo.getAnnotation(Teste.class).value();
                try {
                    Object instancia = classe.getDeclaredConstructor().newInstance();
                    metodo.invoke(instancia);
                    System.out.println("  [OK]    " + descricao);
                    ok++;
                } catch (InvocationTargetException e) {
                    System.out.println("  [FALHA] " + descricao + "\n          -> " + e.getCause());
                    falhas++;
                }
            }
        }

        System.out.println("\nResultado: " + ok + " passaram, " + falhas + " falharam.");
        if (falhas > 0) {
            System.exit(1);
        }
    }
}
