package com.maddenabbott.jcommander.spring;

import com.beust.jcommander.JCommander;
import com.maddenabbott.jcommander.controller.Command;
import com.maddenabbott.jcommander.controller.JCommanderController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConditionalOnNotWebApplication
@Configuration
@EnableConfigurationProperties(JCommanderProperties.class)
public class JCommanderAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public JCommander jCommander() {
    return new JCommander();
  }

  @Bean
  @ConditionalOnMissingBean
  public JCommanderController jCommanderController(
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final List<Command> commands,
    final JCommander jCommander
  ) {
    commands.forEach(jCommander::addCommand);
    return new JCommanderController(jCommander);
  }

  @Bean
  @ConditionalOnMissingBean
  public JCommanderRunner jCommanderRunner(final JCommanderController jCommanderController) {
    return new JCommanderRunner(jCommanderController);
  }

}
