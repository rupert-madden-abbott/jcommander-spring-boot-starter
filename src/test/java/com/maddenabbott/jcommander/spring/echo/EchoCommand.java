package com.maddenabbott.jcommander.spring.echo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.maddenabbott.jcommander.controller.Command;
import org.springframework.stereotype.Component;

@Component
@Parameters(commandNames = "echo")
public class EchoCommand implements Command {
  private final EchoService echoService;

  @Parameter(names = "--message")
  private String message;

  public EchoCommand(final EchoService echoService) {
    this.echoService = echoService;
  }

  @Override
  public void run() throws Exception {
    echoService.echo(message);
  }
}
