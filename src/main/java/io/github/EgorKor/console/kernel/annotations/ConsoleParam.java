package io.github.EgorKor.console.kernel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ConsoleParam {
    String value() default "";

    boolean required() default true;

    String defaultValue() default "";
}
