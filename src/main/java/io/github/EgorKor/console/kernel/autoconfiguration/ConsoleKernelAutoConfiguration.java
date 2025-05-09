package io.github.EgorKor.console.kernel.autoconfiguration;


import io.github.EgorKor.console.kernel.core.CommandsRegistry;
import io.github.EgorKor.console.kernel.postprocessor.ConsoleKernelAnnotationsBeanPostProcessor;
import io.github.EgorKor.console.kernel.postprocessor.ConsoleKernelListenerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import io.github.EgorKor.console.kernel.core.listening.ConsoleCommandListener;
import io.github.EgorKor.console.kernel.core.parsing.ConsoleCommandParser;

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
public class ConsoleKernelAutoConfiguration{
    private final Logger logger = LoggerFactory.getLogger(ConsoleKernelAutoConfiguration.class);


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CommandsRegistry registry() {
        return new CommandsRegistry();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void logStartMessage(){
        logger.info("Successful start of console kernel starter, input `Enter` to enable console kernel mode");
    }

    @Bean
    public ConsoleCommandParser commandParser() {
        return new ConsoleCommandParser();
    }

    @Bean
    public ConsoleCommandListener commandListener() {
        return new ConsoleCommandListener();
    }

    @Bean
    public ConsoleKernelListenerRunner runner() {
        return new ConsoleKernelListenerRunner();
    }


    @Bean
    public static ConsoleKernelAnnotationsBeanPostProcessor beanPostProcessor(CommandsRegistry registry) {
        return new ConsoleKernelAnnotationsBeanPostProcessor(registry);
    }

}
