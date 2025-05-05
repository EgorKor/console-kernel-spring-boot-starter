package io.github.EgorKor.console.kernel.core.listening;

import io.github.EgorKor.console.kernel.core.CommandsRegistry;
import io.github.EgorKor.console.kernel.core.interfaces.CommandListener;
import io.github.EgorKor.console.kernel.core.interfaces.CommandParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConsoleCommandListener implements CommandListener {
    private boolean isStarted;
    @Autowired
    private CommandParser commandParser;
    @Autowired
    private CommandsRegistry commandsRegistry;
    private final String consoleKernelThreadName = UUID.randomUUID().toString();
    private ConsoleKernelInvocationHandler invocationHandler;


    @SneakyThrows
    @Override
    public void startListening() {
        if (isStarted) {
            throw new IllegalStateException("ConsoleCommandListener already has been started");
        }
        invocationHandler = new ConsoleKernelInvocationHandler(System.out, consoleKernelThreadName);

        Class<?> proxyType = new ByteBuddy()
                .subclass(PrintStream.class)
                .constructor(ElementMatchers.takesArguments(OutputStream.class))
                .intercept(SuperMethodCall.INSTANCE)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(invocationHandler))
                .make()
                .load(PrintStream.class.getClassLoader())
                .getLoaded();

        PrintStream proxy = (PrintStream) proxyType.
                getConstructor(OutputStream.class)
                .newInstance(System.out);
        System.setOut(proxy);
        System.setErr(System.out);

        Thread thread = new Thread(new CommandListenerRunnable(this,commandParser,commandsRegistry));
        thread.setName(consoleKernelThreadName);
        thread.setDaemon(true);
        thread.start();
        isStarted = true;
    }


    protected void enableConsoleKernelMode() {
        invocationHandler.setEnableDefaultLogging(false);
    }

    protected void disableConsoleKernelMode() {
        invocationHandler.setEnableDefaultLogging(true);
    }

    @RequiredArgsConstructor
    public static class ConsoleKernelInvocationHandler implements InvocationHandler {
        private final Object target;
        private boolean enableDefaultLogging = true;
        private final List<MethodCall> accumulatedCalls = new ArrayList<>();
        private final String consoleKernelThreadName;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Thread currentThread = Thread.currentThread();
            if (currentThread.getName().equals(consoleKernelThreadName)) {
                return method.invoke(target, args);
            }
            if (!enableDefaultLogging) {
                accumulatedCalls.add(new MethodCall(method, args));
                return null;
            }
            return method.invoke(target, args);
        }

        @SneakyThrows
        public void setEnableDefaultLogging(boolean enableDefaultLogging) {
            if (enableDefaultLogging) {
                for (MethodCall call : accumulatedCalls) {
                    call.method().invoke(target, call.args());
                }
            }
            this.enableDefaultLogging = enableDefaultLogging;
        }
    }

    private record MethodCall(Method method, Object[] args) {
    }

}
