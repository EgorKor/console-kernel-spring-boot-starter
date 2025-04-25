package ru.korovin.console.kernel.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleParam {
    String value() default "";

    boolean required() default true;

    String defaultValue() default "";
}
