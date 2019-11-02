package com.maddenabbott.jcommander.spring;

import com.maddenabbott.jcommander.controller.JCommanderController;
import org.springframework.boot.CommandLineRunner;

public class JCommanderRunner implements CommandLineRunner {
  private final JCommanderController jCommanderController;

  public JCommanderRunner(final JCommanderController jCommanderController) {
    this.jCommanderController = jCommanderController;
  }

  @Override
  public void run(final String... args) throws Exception {
    jCommanderController.execute(args);
  }
}
