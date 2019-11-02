package com.maddenabbott.jcommander.spring.echo;

import org.springframework.stereotype.Component;

@Component
public class EchoService {
  public void echo(final String message) {
    System.out.println(message);
  }
}
