package com.maddenabbott.jcommander.spring.echo;

import com.beust.jcommander.Parameters;
import com.maddenabbott.jcommander.controller.Command;
import org.springframework.stereotype.Component;

@Component
@Parameters(commandNames = "no-error-message")
public class NoErrorMessageCommand implements Command {

    @Override
    public void run() throws Exception {
        throw new Exception();
    }
}
