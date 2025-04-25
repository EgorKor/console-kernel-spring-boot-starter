package ru.korovin.console.kernel.core.interfaces;

import ru.korovin.console.kernel.annotations.ConsoleMethod;
import ru.korovin.console.kernel.core.parsing.ParamDefinition;

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
