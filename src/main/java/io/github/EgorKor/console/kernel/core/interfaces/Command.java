package io.github.EgorKor.console.kernel.core.interfaces;

import io.github.EgorKor.console.kernel.annotations.ConsoleMethod;
import io.github.EgorKor.console.kernel.core.parsing.ParamDefinition;

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
