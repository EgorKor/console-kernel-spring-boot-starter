package ru.korovin.core.interfaces;

import ru.korovin.annotations.ConsoleMethod;
import ru.korovin.core.impl.ParamDefinition;

import java.lang.reflect.Method;
import java.util.Map;

public interface Command {
    ConsoleMethod getConsoleMethodInfo();
    String getCommandName();
    Class<?> getCommandParentClass();
    Method getMethod();
    Object getCommandComponentInstance();
    Map<String, ParamDefinition> getParamDefinitions();
}
