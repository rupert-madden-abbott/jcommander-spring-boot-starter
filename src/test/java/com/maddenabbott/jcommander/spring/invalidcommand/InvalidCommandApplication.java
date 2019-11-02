package com.maddenabbott.jcommander.spring.invalidcommand;

import com.beust.jcommander.JCommander;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InvalidCommandApplication {
  public static void main(String[] args) {
    SpringApplication.run(InvalidCommandApplication.class, args);
  }

  @Bean
  public JCommander jCommander() {
    JCommander jCommander = new JCommander();
    jCommander.addCommand(new InvalidCommand());
    return jCommander;
  }
}
