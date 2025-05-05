package io.github.EgorKor.console.kernel.core;

import io.github.EgorKor.console.kernel.core.interfaces.Command;
import lombok.Builder;
import io.github.EgorKor.console.kernel.annotations.ConsoleMethod;
import io.github.EgorKor.console.kernel.core.parsing.ParamDefinition;

import java.lang.reflect.Method;
import java.util.Map;

@Builder
public class ConsoleCommand implements Command {
    private Class<?> commandParentClass;
    private Method commandMethod;
    private Object commandComponentInstance;
    private Map<String, ParamDefinition> paramDefinitions;
    private ConsoleMethod consoleMethod;
    private String command;

    @Override
    public ConsoleMethod getConsoleMethodInfo() {
        return consoleMethod;
    }

    @Override
    public String getCommandName() {
        return command;
    }

    @Override
    public Class<?> getCommandParentClass() {
        return commandParentClass;
    }

    @Override
    public Method getMethod() {
        return commandMethod;
    }

    @Override
    public Object getCommandComponentInstance() {
        return commandComponentInstance;
    }

    @Override
    public Map<String, ParamDefinition> getParamDefinitions() {
        return paramDefinitions;
    }

    @Override
    public String toString() {
        return command + (consoleMethod.hint().isBlank() ? "" : (" - " + consoleMethod.hint()));
    }
}
