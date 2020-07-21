package com.maddenabbott.jcommander.spring.echo;

import com.beust.jcommander.Parameters;
import com.maddenabbott.jcommander.controller.Command;
import org.springframework.stereotype.Component;

@Component
@Parameters(commandNames = "nested-error-message")
public class NestedErrorMessageCommand implements Command {

    @Override
    public void run() {
        try {
            throw new Exception("nested error");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
