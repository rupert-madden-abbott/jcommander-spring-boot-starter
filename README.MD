# JCommander Spring Boot Starter

## Change Log

* 0.1.1-SNAPSHOT
  * Change licence from MIT to Apache for consistency with Spring Boot and JCommander.
  * Bump to Spring Boot 2.2.7.RELEASE
* 0.1.0
  * Initial release compatible with Spring Boot 2.2.X, JCommander 1.X and Java 8+.

## Quick Start

Add this dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.madden-abbott</groupId>
  <artifactId>jcommander-spring-boot-starter</artifactId>
  <version>0.1.0</version>
</dependency>
```

Create a standard Spring Boot application:

```java
@SpringBootApplication
public class HelloWorldApplication {
  public static void main(String[] args) {
    SpringApplication.run(HelloWorldApplication.class, args);
  }
}
```
    
Implement the `Command` interface and add the @Parameters annotation:

```java
@SpringBootApplication
@Parameters(commandNames = "hello-world")
public class HelloWorldApplication implements Command {
  public static void main(String[] args) {
    SpringApplication.run(HelloWorldApplication.class, args);
  }
  
  @Override
  public void run() {
    System.out.println("Hello, World!");
  }
}
```

Execute by passing in the name of the command:

```shell script
mvn package
java -jar target/*.jar hello-world
```

## Multiple Commands

To have multiple commands, simply have multiple classes implementing `Command` with the
`@Parameters` annotation and configure them as Spring beans. The easiest way would be to add the
`@Component` annotation. Then select the command by passing the matching command name via the
command line.

```java
@SpringBootApplication
public class MultiCommandApplication {
  public static void main(String[] args) {
    SpringApplication.run(MultiCommandApplication.class, args);
  }
}
```

```java
@Component
@Parameters(commandNames = "command-one")
public class CommandOne implements Command {
  @Override
  public void run() {
    System.out.println("This is command one.");
  }
}
```

```java
@Component
@Parameters(commandNames = "command-two")
public class CommandTwo implements Command {
  @Override
  public void run() {
    System.out.println("This is command two.");
  }
}
```

## Command Line Argument Parsing

All of JCommander's command line argument parsing works as normal:

```java
@Component
@Parameters(commandNames = "example")
public class ExampleCommand implements Command {
  @Parameter(name = "--message")
  private String message;

  @Override
  public void run() {
    System.out.println(message);
  }
}
```

See the [full JCommander documentation](http://jcommander.org/).

## Dependency Injection

`Command`s are just regular Spring beans and so can have dependencies injected into them in
the normal way:

```java
@Component
@Parameters(commandNames = "example")
public class ExampleCommand implements Command {
  private final ExampleService exampleService;
  
  public ExampleCommand(final ExampleService exampleService) {
    this.exampleService = exampleService; 
  }

  @Override
  public void run() {
    exampleService.execute();
  }
}
```

## JCommander Configuration

By default, a `JCommander` instance is automatically created and all `Command` beans are added to
it. You can add your own customisation by adding your own JCommander bean:

```java
@Configuration
public class ExampleConfiguration {
  @Bean
  public JCommander jCommander() {
    JCommander jCommander = new JCommander();
    //Your configuration here...
    return jCommander;
  }
}
```

Note that this won't disable the automatic adding of all `Command` beans to this JCommander
instance.

## Exception Handling

It is recommended to just let all exceptions get caught by the Spring Boot wrapper which will
ensure they get appropriately logged.

`Command`s can throw both checked and unchecked exceptions if required.

## Exit Status Handling

By default, Spring Boot will return an exit status of 0 if the application finishes without an
exception being thrown. Otherwise, it will return an exit status of 1.

If a command has failed but you don't want to fail immediately, it is recommended to catch the
exception and then rethrow at a later point:

```java
@Component
@Parameters(commandNames = "example")
public class ExampleCommand implements Command {
  private final ExampleSupplier supplier;
  private final ExampleConsumer consumer;

  public ExampleCommand(ExampleSupplier supplier, ExampleConsumer consumer) {
    this.supplier = supplier;
    this.consumer = consumer;
  }

  @Override
  public void run() {
    boolean errorEncountered = false;
    for (Example example : supplier.getAll()) {
      try {
        consumer.consume(example);
      } catch (Exception e) {
        //log exception appropriately here.
        errorEncountered = true;
      }
    }

    if (errorEncountered) {
      throw new RuntimeException("An error was encountered. See previous logs for more information.");
    }
  }
}
```

If you want more control over the exit code, you can implement `ExitCodeGenerator`. This can be
done via a custom exception, in which case if you throw that exception, then whatever integer you
specify in that exception will get returned as the exit status.

```java
public class ExampleException extends Exception implements ExitCodeGenerator {
  @Override
  int getExitCode() {
    return 5;
  }
}
```

```java
@Component
@Parameters(commandNames = "example")
public class ExampleCommand implements Command {
  @Override
  public void run() {
    //Exit status will be 5
    throw new ExampleException();
  }
}
```

Alternatively, you can have your
commands implement `ExitCodeGenerator` and not throw an exception but then you have to handle
exiting with that status code yourself in your main method:

```java
@SpringBootApplication
public class ExampleApplication {
  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(ExampleApplication.class, args)));
  }
}
```

```java
@Component
@Parameters(commandNames = "example")
public class ExampleCommand implements Command, ExitCodeGenerator {
  private final ExampleSupplier supplier;

  private final ExampleConsumer consumer;
  
  private int status = 0;

  @Override
  public void run() {
    for (Example example : supplier.getAll()) {
      try {
        consumer.consume(example);
      } catch (Exception e) {
        //log exception appropriately here.
        status = 5;
      }
    }
  }
  @Override
  int getExitCode() {
    return status;
  }

}
```
