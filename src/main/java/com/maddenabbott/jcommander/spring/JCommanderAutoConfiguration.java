package com.maddenabbott.jcommander.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.beust.jcommander.JCommander;
import com.maddenabbott.jcommander.controller.Command;
import com.maddenabbott.jcommander.controller.JCommanderController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnNotWebApplication
@Configuration
public class JCommanderAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public JCommander jCommander() {
    return new JCommander();
  }

  @Bean
  @ConditionalOnMissingBean
  public JCommanderController jCommanderController(
    final Optional<List<Command>> commands,
    final JCommander jCommander
  ) {
    commands.orElseGet(ArrayList::new).forEach(jCommander::addCommand);
    return new JCommanderController(jCommander);
  }

  @Bean
  @ConditionalOnMissingBean
  public JCommanderRunner jCommanderRunner(final JCommanderController jCommanderController) {
    return new JCommanderRunner(jCommanderController);
  }

}
