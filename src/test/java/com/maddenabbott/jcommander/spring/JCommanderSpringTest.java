package com.maddenabbott.jcommander.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.maddenabbott.jcommander.spring.echo.EchoApplication;
import com.maddenabbott.jcommander.spring.invalidcommand.InvalidCommandApplication;
import com.maddenabbott.jcommander.spring.nocommand.NoCommandApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class JCommanderSpringTest {

  @Test
  void shouldExecuteSelectedCommand(CapturedOutput output) {
    EchoApplication.main(new String[] {"echo", "--message", "hello"});
    assertThat(output).contains("hello");
  }

  @Test
  void shouldThrowOnInvalidCommandSelected(CapturedOutput output) {
    assertThrows(RuntimeException.class, () -> EchoApplication.main(new String[] {"invalid"}));
    assertThat(output).contains("Caused by: com.beust.jcommander.MissingCommandException: Expected a command, got invalid");
  }

  @Test
  void shouldThrowOnNoCommandLineArgumentsProvided(CapturedOutput output) {
    assertThrows(RuntimeException.class, () -> EchoApplication.main(new String[] {}));
    assertThat(output).contains("Caused by: com.beust.jcommander.ParameterException: No command line arguments were provided.");
  }

  @Test
  void shouldThrowOnNoCommandBeans(CapturedOutput output) {
    assertThrows(RuntimeException.class, () -> NoCommandApplication.main(new String[] {}));
    assertThat(output).contains("Caused by: com.beust.jcommander.ParameterException: No command objects were provided.");
  }

  @Test
  void shouldThrowOnSelectedCommandNotImplementingCommand(CapturedOutput output) {
    assertThrows(RuntimeException.class, () -> InvalidCommandApplication.main(new String[] { "invalid" }));
    assertThat(output).contains("Caused by: com.beust.jcommander.ParameterException: The selected command 'invalid' does not implement the Command interface.");
  }
}
