package ru.korovin.console.kernel.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ConsoleMethod {
    String command() default  "";
    String hint() default "";
}
