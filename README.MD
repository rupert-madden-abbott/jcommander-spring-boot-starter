# JCommander Spring Boot Starter

## Change Log

* 0.2.0-SNAPSHOT
  * Bump to Spring Boot 2.3.1.RELEASE
  * Support clean output e.g. for piping output to other applications
* 0.1.1
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
  <version>0.1.1</version>
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
    jCommander.setVerbose(1);
    return jCommander;
  }
}
```

Note that any `Command` beans will still automatically be added to this JCommander instance.

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

## Output

_Since 0.2.0_

By default, Java logging and Spring Boot logging is quite verbose. This breaks
[the rule of silence](http://www.linfo.org/rule_of_silence.html) and will surprise and annoy users of command line
applications. Unfortunately, suppressing or redirecting this output may also confuse and surprise Java developers when
developing or debugging those applications.

This starter lets you opt in to having less verbose output. You should make use of this when distributing your
application to end users but may wish to leave them off when developing and debugging your application. One way to do
that is to switch them on by default and then deactivate them via a Spring profile.

The basic strategy is to redirect all log output to a file. Any output that you do want to go to the console, should be
sent to either standard error or standard out. This is unusual advice for most Java applications but is the best way to
manage what output your users see.

Add the following to `src/main/resources/application.yaml`:

    spring:
      application:
        name: "change_me"
    
    ---
    
    spring:
      profiles: "!debug"
      main:
        banner-mode: off
    logging:
      pattern:
        console:
      file:
        name: ${java.io.tmpdir}/${spring.application.name}.log
    jcommander:
      output-errors: true

1) Change `spring.application.name`. This will be the name of your log file.
2) You can disable the remaining configuration by switching on the `debug` profile in case something goes wrong.
3) The banner is switched off as this is not helpful in a log file and we don't want it on the console.
4) The logging pattern for the console is blanked out, causing nothing to be logged to the console
5) The logging file name is set to the system temp directory. All logging will go here.
6) The JCommander starter is instructed to output any uncaught exceptions to standard error. Only error messages will be output.

In this way, you are in full control of what your users see but can still check the log file for the full details of
what happened. Whilst developing, you can run with `-spring.active.profiles=debug` to see everything on the console as
normal.

Your commands should output messages to standard out. They should either throw exceptions with messages you are happy
can be displayed to standard error, or should catch and print to standard error directly.

If you also want to display usage information along with an error, wrap your exception in `InvalidUseException`.
