package ru.korovin.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleParam {
    String value() default "";

    boolean required() default false;

    String defaultValue() default "";
}
