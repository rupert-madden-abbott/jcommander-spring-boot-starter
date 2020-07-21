package com.maddenabbott.jcommander.spring;

import org.springframework.boot.SpringBootExceptionReporter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

import static com.maddenabbott.jcommander.spring.StandardErrorSpringBootExceptionReporter.ORDER;

@Order(ORDER)
public class StandardErrorSpringBootExceptionReporter implements SpringBootExceptionReporter {
    //Ensure this runs before the default FailureAnalyzers
    public static final int ORDER = -1;

    private final ApplicationContext context;

    public StandardErrorSpringBootExceptionReporter(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean reportException(Throwable failure) {
        if (!isOutputErrors()) {
            return false;
        }

        if (isSpringBootWrapped(failure)) {
            failure = failure.getCause();
        }

        System.err.println(failure.getMessage());
        return false;
    }

    private boolean isSpringBootWrapped(Throwable failure) {
        return failure instanceof IllegalStateException
                && "Failed to execute CommandLineRunner".equals(failure.getMessage());
    }

    private boolean isOutputErrors() {
        return context.getEnvironment().getProperty("jcommander.output-errors", Boolean.class, false);
    }
}
