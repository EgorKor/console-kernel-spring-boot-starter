package io.github.EgorKor.console.kernel.core.parsing;

import io.github.EgorKor.console.kernel.annotations.ConsoleParam;
import io.github.EgorKor.console.kernel.core.CommandsRegistry;
import io.github.EgorKor.console.kernel.core.interfaces.Command;
import io.github.EgorKor.console.kernel.core.interfaces.CommandParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.*;

public class ConsoleCommandParser implements CommandParser {
    @Autowired
    private CommandsRegistry commandsRegistry;

    @Override
    public boolean parseAndExecuteCommand(String command) {
        if (command.trim().isBlank()) {
            throw new IllegalArgumentException("Command could not be empty or blank");
        }
        if (command.trim().equalsIgnoreCase("exit")) {
            return false;
        }
        if (command.trim().equalsIgnoreCase("help")){
            System.out.println("Available commands");
            commandsRegistry.getAllCommands().forEach(System.out::println);
            return true;
        }

        ParsingScanner parsingScanner = new ParsingScanner(command);
        Token commandToken = parsingScanner.getCommandToken();
        if (!commandsRegistry.containsCommand(commandToken.token())) {
            throw new IllegalArgumentException("Unrecognised command - " + commandToken.token());
        }

        Command commandObj = commandsRegistry.getCommand(commandToken.token());
        Map<String, ParamDefinition> paramDefinitionMap = commandObj.getParamDefinitions();
        Map<String, String> params = new HashMap<>();
        // Инициализация параметров значениями по умолчанию
        paramDefinitionMap.forEach((key, definition) -> {
            ConsoleParam consoleParam = definition.getConsoleParam();
            if (consoleParam != null && !consoleParam.defaultValue().isBlank()) {
                params.put(key, consoleParam.defaultValue());
            } else {
                params.put(key, null);
            }
        });

        Token lastParsed = commandToken;
        Token parsedToken = null;
        while (parsingScanner.hasToken()) {
            parsedToken = parsingScanner.nextToken();
            switch (parsedToken.type()) {
                case PARAM -> {
                    String paramName = parsedToken.token().substring(1);
                    if (!params.containsKey(paramName)) {
                        throw new IllegalArgumentException("Unrecognised param - " + paramName);
                    }

                    if (Objects.requireNonNull(lastParsed.type()) == TokenType.PARAM) {
                        params.put(lastParsed.token().substring(1), "true");
                    }
                }
                case PARAM_VALUE -> {
                    switch (lastParsed.type()) {
                        case PARAM -> {
                            params.put(lastParsed.token().substring(1), parsedToken.token());
                        }
                        case PARAM_VALUE -> {
                            throw new IllegalArgumentException("Repeat of value of param");
                        }
                        case COMMAND -> {
                            throw new IllegalArgumentException("Param value after command is illegal");
                        }
                    }
                }
            }
            if (parsingScanner.hasToken()) {
                lastParsed = parsedToken;
            }
        }
        switch (lastParsed.type()) {
            case COMMAND -> {
                if (parsedToken != null && parsedToken.type() == TokenType.PARAM) {
                    params.put(parsedToken.token().substring(1), "true");
                }
                if (parsedToken != null && parsedToken.type() != TokenType.PARAM) {
                    throw new IllegalArgumentException("Not param after command is illegal");
                }
            }
            case PARAM -> {
                if (parsedToken != null) {
                    params.put(parsedToken.token().substring(1), "true");
                }
            }
            case PARAM_VALUE -> {
                if (parsedToken != null) {
                    params.put(lastParsed.token().substring(1), parsedToken.token());
                }
            }
        }


        List<ParamDefinition> sortedParamDefinitions = paramDefinitionMap
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().getIndex()))
                .map(Map.Entry::getValue)
                .toList();
        Object[] sortedArgs = sortedParamDefinitions
                .stream()
                .map(ParamDefinition::getParamName)
                .map(o -> paramDefinitionMap.get(o).applyToParam(params.get(o)))
                .toList()
                .toArray();
        Method commandMethod = commandObj.getMethod();
        Object commandInvocationInstance = commandObj.getCommandComponentInstance();
        try {
            commandMethod.invoke(commandInvocationInstance, sortedArgs);
        } catch (Exception e) {
            System.out.println("Error occur while executing command " + commandToken.token() + " : " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }


}
