package ru.korovin.console.kernel.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CommandComponent {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}
