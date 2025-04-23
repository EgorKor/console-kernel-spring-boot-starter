package ru.korovin.core.impl.listening;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import ru.korovin.core.impl.CommandsRegistry;
import ru.korovin.core.interfaces.CommandParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

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
                .completer(new AggregateCompleter(createCompleterArray()))
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

    private Completer[] createCompleterArray() {
        Completer mainCompleter = new ArgumentCompleter(
                new StringsCompleter("help", "exit")
        );
        List<Completer> commandCompleterList = new ArrayList<>();
        commandsRegistry.getAllCommands().forEach(command -> {
                    List<Completer> completers = new ArrayList<>(command.getParamDefinitions()
                            .keySet()
                            .stream()
                            .map(paramDefinition ->
                                    new StringsCompleter("-" + paramDefinition)
                            ).toList());
                    completers.add(0, new StringsCompleter(command.getCommandName()));

                    List<ArgumentCompleter> repeatedCompleter = IntStream.rangeClosed(0, command.getParamDefinitions().size())
                            .boxed()
                            .map((s) -> new ArgumentCompleter(completers.toArray(new Completer[0])))
                            .toList();
                    AggregateCompleter aggregateCompleter = new AggregateCompleter(
                            repeatedCompleter.toArray(new Completer[0])
                    );
                    commandCompleterList.add(aggregateCompleter);
                }
        );
        commandCompleterList.add(0, mainCompleter);
        return commandCompleterList.toArray(new Completer[0]);
    }

}
