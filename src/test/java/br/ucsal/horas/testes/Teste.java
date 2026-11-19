package br.ucsal.horas.testes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marca um método como teste. O {@link ExecutorTestes} descobre estes métodos por reflexão. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Teste {

    /** Descrição legível do que o teste garante. */
    String value();
}
