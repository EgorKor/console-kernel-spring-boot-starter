package io.github.EgorKor.console.kernel.core;

import io.github.EgorKor.console.kernel.core.interfaces.Command;

import java.util.*;

public class CommandsRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void addCommand(String commandKey, Command command){
        commands.put(commandKey, command);
    }

    public Command getCommand(String command){
        return commands.get(command);
    }

    public boolean containsCommand(String command){
        return commands.containsKey(command);
    }

    public List<Command> getAllCommands(){
        return new ArrayList<>(commands.values());
    }
}
