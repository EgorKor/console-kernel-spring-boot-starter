package io.github.EgorKor.console.kernel.core.listening;

import io.github.EgorKor.console.kernel.core.CommandsRegistry;
import io.github.EgorKor.console.kernel.core.interfaces.Command;
import io.github.EgorKor.console.kernel.core.interfaces.CommandParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class CommandListenerRunnable implements Runnable {
    private final ConsoleCommandListener listener;
    private final CommandParser commandParser;
    private final CommandsRegistry commandsRegistry;

    @SneakyThrows
    @Override
    public void run() {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(createCompleter())
                .history(new DefaultHistory())
                .build();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            scanner.nextLine();
            boolean isRunning = true;
            System.out.println("Console Kernel was enable successfully.\nInput `help` to see all commands.\nInput `exit` for disabling console mode.");
            listener.enableConsoleKernelMode();
            while (isRunning) {
                String command = reader.readLine(" > ");
                try {
                    isRunning = commandParser.parseAndExecuteCommand(command);
                } catch (Exception e) {
                    System.out.println("Error of command parsing: " + e.getMessage());
                }
            }
            System.out.println("Console Kernel Mode was deactivated successfully. Input `Enter` to enable it again.");
            listener.disableConsoleKernelMode();
        }
    }

    private Completer createCompleter() {
        // Сначала собираем все имена команд
        List<String> allCommandNames = new ArrayList<>();
        allCommandNames.add("help");
        allCommandNames.add("exit");
        commandsRegistry.getAllCommands().forEach(command ->
                allCommandNames.add(command.getCommandName()));

        return (reader, line, candidates) -> {
            List<String> words = line.words();

            if (words.isEmpty()) {
                allCommandNames.forEach(cmd -> candidates.add(new Candidate(cmd)));
                return;
            }

            if (words.size() == 1) {
                String partial = words.get(0);
                allCommandNames.stream()
                        .filter(cmd -> cmd.startsWith(partial))
                        .forEach(cmd -> candidates.add(new Candidate(cmd)));
                return;
            }

            String commandName = words.get(0);
            if (commandName.equals("help") || commandName.equals("exit")) {
                return;
            }

            if (commandsRegistry.containsCommand(commandName)) {
                Command command = commandsRegistry.getCommand(commandName);
                List<String> parameters = command.getParamDefinitions().keySet().stream()
                        .map(p -> "-" + p)
                        .toList();

                parameters.stream()
                        .filter(p -> !words.contains(p))
                        .forEach(p -> candidates.add(new Candidate(p)));

            }
        };
    }

}
