package ru.korovin.console.kernel.core.parsing;

import lombok.Getter;
import ru.korovin.console.kernel.annotations.ConsoleParam;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Getter
public class ParamDefinition {
    private final ConsoleParam consoleParam;
    private final Class<?> paramType;
    private final String paramName;
    private final int index;

    private static Map<Class<?>, Function<String, ?>> mappers = new HashMap<>();
    static {
        mappers.put(Integer.class, Integer::valueOf);
        mappers.put(Byte.class, Byte::valueOf);
        mappers.put(Short.class, Short::valueOf);
        mappers.put(Long.class, Long::valueOf);
        mappers.put(Float.class, Float::valueOf);
        mappers.put(Double.class, Double::valueOf);
        mappers.put(Boolean.class, Boolean::valueOf);
        mappers.put(String.class, String::valueOf);
    }

    public ParamDefinition(ConsoleParam consoleParam, Class<?> paramType, Parameter parameter, int index) {
        this.consoleParam = consoleParam;
        this.paramType = paramType;
        this.index = index;
        if(consoleParam != null){
            paramName = !consoleParam.value().isBlank() ? consoleParam.value() : parameter.getName();
        }else {
            paramName = parameter.getName();
        }
    }

    public Object applyToParam(String arg){
        if(arg == null && consoleParam != null && consoleParam.required()){
            throw new IllegalStateException("Missing required param - " + paramName);
        }
        if(arg != null){
            return mappers.get(paramType).apply(arg);
        }
        return null;
    }


}
