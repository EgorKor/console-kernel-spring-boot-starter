package ru.korovin.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;
import ru.korovin.annotations.CommandComponent;
import ru.korovin.annotations.ConsoleMethod;
import ru.korovin.annotations.ConsoleParam;
import ru.korovin.core.impl.CommandsRegistry;
import ru.korovin.core.impl.ConsoleCommand;
import ru.korovin.core.impl.ParamDefinition;
import ru.korovin.core.interfaces.Command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Order
@RequiredArgsConstructor
public class ConsoleKernelAnnotationsBeanPostProcessor implements BeanPostProcessor {
    private Map<String, Object> originalBeans = new HashMap<>();

    private final CommandsRegistry commandsRegistry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass() == null) {
            return bean;
        }
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(CommandComponent.class)) {
            originalBeans.put(beanName, bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!originalBeans.containsKey(beanName) || bean.getClass() == null) {
            return bean;
        }
        Class<?> originBeanClass = originalBeans.get(beanName).getClass();
        Class<?> proxyClass = bean.getClass();
        for (Method method : originBeanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConsoleMethod.class)) {
                validateConsoleMethodArgumentsTypes(method, originBeanClass);
                validateUniqueCommandConstraint(method, originBeanClass);
                ConsoleMethod annotationInfo = method.getAnnotation(ConsoleMethod.class);

                Method proxyMethod = ReflectionUtils.findMethod(proxyClass,
                        method.getName(),
                        method.getParameterTypes());

                Command command = ConsoleCommand.builder()
                        .commandParentClass(originBeanClass)
                        .commandComponentInstance(bean)
                        .commandMethod(proxyMethod)
                        .paramDefinitions(parseParams(method))
                        .consoleMethod(annotationInfo)
                        .command(annotationInfo.command().isBlank() ? method.getName() : annotationInfo.command())
                        .build();

                commandsRegistry.addCommand(command.getCommandName(), command);
            }
        }
        return bean;
    }

    private void validateConsoleMethodArgumentsTypes(Method method, Class<?> beanClass) {
        Set<Class<?>> allowedTypes = Set.of(Byte.class, Short.class, Integer.class,
                Long.class, String.class, Boolean.class, Float.class, Double.class);
        for (Parameter parameter : method.getParameters()) {
            if (!allowedTypes.contains(parameter.getType())) {
                throw new IllegalArgumentException("Illegal argument type in Console Method %s, in class %s"
                        .formatted(method.getName(), beanClass.getName()));
            }
        }
    }

    private void validateUniqueCommandConstraint(Method method, Class<?> beanClass) {
        ConsoleMethod consoleMethodInfo = method.getAnnotation(ConsoleMethod.class);
        if (commandsRegistry.containsCommand(consoleMethodInfo.command())) {
            throwUniqueCommandConstraintViolationException(
                    consoleMethodInfo.command(),
                    commandsRegistry.getCommand(consoleMethodInfo.command()).getCommandParentClass(),
                    beanClass,
                    commandsRegistry.getCommand(consoleMethodInfo.command()).getMethod(),
                    method
            );
        }
    }

    private void throwUniqueCommandConstraintViolationException(String commandName,
                                                                Class<?> existingClass,
                                                                Class<?> duplicateClass,
                                                                Method existingMethod,
                                                                Method duplicateMethod) {
        String messageFormat = "`%s` command name violates unique command name constraint." +
                               "Duplicate was found in classes %s and %s, methods %s and %s";
        throw new IllegalArgumentException(messageFormat.formatted(
                commandName,
                existingClass.getName(),
                duplicateClass.getName(),
                existingMethod.getName(),
                duplicateMethod.getName()
        ));
    }

    private Map<String, ParamDefinition> parseParams(Method method) {
        Map<String, ParamDefinition> params = new HashMap<>();
        int paramIndex = 0;
        for (Parameter param : method.getParameters()) {
            ParamDefinition paramDefinition = new ParamDefinition(
                    param.getAnnotation(ConsoleParam.class),
                    param.getType(),
                    param,
                    paramIndex
            );
            params.put(paramDefinition.getParamName(), paramDefinition);
            paramIndex++;
        }
        return params;
    }
}
