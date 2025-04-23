package ru.korovin.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.korovin.core.impl.CommandsRegistry;
import ru.korovin.core.impl.listening.ConsoleCommandListener;
import ru.korovin.core.impl.parsing.ConsoleCommandParser;
import ru.korovin.processing.ConsoleKernelAnnotationsBeanPostProcessor;
import ru.korovin.processing.ConsoleKernelListenerRunner;

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
