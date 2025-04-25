# console-kernel-spring-boot-starter

This starter adding to spring web applications possibility 
to create console commands with beans and execute it
right from terminal. 

Create command component bean

````java
import ru.korovin.console.kernel.annotations.CommandComponent;

@CommandComponent
public class Commands{
    ...
}
````

Declare console method in command component class

```java
import ru.korovin.console.kernel.annotations.CommandComponent;
import ru.korovin.console.kernel.annotations.ConsoleMethod;

@CommandComponent
public class Commands {
    @ConsoleMethod
    public void printNumber(Integer value){
        System.out.println(value);
    }
}

```
Enable console kernel mode with input 'Enter' after context application start working
and get system terminal autocomplete feature

```
Console Kernel was enable successfully.
Input `help` to see all commands.
Input `exit` for disabling console mode.
 > printNumber
checkParam    command1      command2      exit          help          printNumber
```

*WARNING*: Autocomplete would not work in IntellijIDEA or another non system terminal


Use annotation to control parameter parsing behaviour

```java
import ru.korovin.console.kernel.annotations.CommandComponent;
import ru.korovin.console.kernel.annotations.ConsoleMethod;
import ru.korovin.console.kernel.annotations.ConsoleParam;

@CommandComponent
public class Commands {
    @ConsoleMethod
    public void printNumber(@ConsoleParam(required = false, defaultValue = "10") Integer value) {
        System.out.println(value);
    }
    
    @ConsoleMethod(command = "print_string")
    public void printString(@ConsoleParam("value_alias") String value){
        System.out.println(value);
    }
}

```