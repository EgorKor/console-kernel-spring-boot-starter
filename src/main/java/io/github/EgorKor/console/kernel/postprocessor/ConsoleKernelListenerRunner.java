package io.github.EgorKor.console.kernel.postprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import io.github.EgorKor.console.kernel.core.interfaces.CommandListener;

@Order
public class ConsoleKernelListenerRunner implements CommandLineRunner {
    @Autowired
    private CommandListener commandListener;

    @Override
    public void run(String... args) throws Exception {
        commandListener.startListening();
    }
}
