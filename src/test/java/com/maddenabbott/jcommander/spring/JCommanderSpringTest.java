package com.maddenabbott.jcommander.spring;

import com.maddenabbott.jcommander.controller.InvalidUseException;
import com.maddenabbott.jcommander.spring.echo.EchoApplication;
import com.maddenabbott.jcommander.spring.invalidcommand.InvalidCommandApplication;
import com.maddenabbott.jcommander.spring.nocommand.NoCommandApplication;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
class JCommanderSpringTest {

    @Test
    void shouldExecuteSelectedCommand(CapturedOutput output) {
        EchoApplication.main(new String[]{"echo", "--message", "hello"});
        assertThat(fixBugSpringBoot21056(output)).startsWith("hello");
    }

    @Test
    void shouldThrowOnNoCommandBeans(CapturedOutput output) {
        assertIllegalState("No Command implementing beans have been defined.", output,
                () -> NoCommandApplication.main(new String[]{"test"})
        );
    }

    @Test
    void shouldThrowOnSelectedCommandNotImplementingCommand(CapturedOutput output) {
        assertIllegalState("The selected command 'invalid' does not implement the Command interface.", output,
                () -> InvalidCommandApplication.main(new String[]{"invalid"})
        );
    }

    @Test
    void shouldThrowOnNoCommandLineArgumentsProvided(CapturedOutput output) {
        assertInvalidUse("No command line arguments were provided.", output,
                () -> EchoApplication.main(new String[]{})
        );
    }

    @Test
    void shouldThrowOnInvalidCommandSelected(CapturedOutput output) {
        assertInvalidUse("Expected a command, got invalid", output,
                () -> EchoApplication.main(new String[]{"invalid"})
        );
    }

    @Test
    void shouldDisplayVerboseOutput(CapturedOutput output) {
        assertThatThrownBy(() -> new SpringApplicationBuilder(EchoApplication.class)
                .profiles("debug")
                .run(new String[]{"invalid"})
        ).hasCauseExactlyInstanceOf(InvalidUseException.class);

        assertThat(output).doesNotStartWith("Expected a command, got invalid")
                .contains("Expected a command, got invalid");
    }

    @Test
    void shouldDisplayErrorMessage(CapturedOutput output) {
        assertThatThrownBy(() -> EchoApplication.main(new String[]{"error-message"}));
        assertThat(fixBugSpringBoot21056(output)).startsWith("error");
    }

    @Test
    void shouldDisplayNestedErrorMessage(CapturedOutput output) {
        assertThatThrownBy(() -> EchoApplication.main(new String[]{"nested-error-message"}));
        assertThat(fixBugSpringBoot21056(output)).startsWith("java.lang.Exception: nested error");
    }

    @Test
    void shouldDisplayExceptionWhenNoErrorMessage(CapturedOutput output) {
        assertThatThrownBy(() -> EchoApplication.main(new String[]{"no-error-message"}));
        assertThat(fixBugSpringBoot21056(output)).startsWith("Error! Exception.");
    }

    private void assertInvalidUse(String message, CapturedOutput output, ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).hasCauseExactlyInstanceOf(InvalidUseException.class);
        assertThat(fixBugSpringBoot21056(output)).startsWith(message);
    }

    private void assertIllegalState(String message, CapturedOutput output, ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).hasCauseExactlyInstanceOf(IllegalStateException.class);
        assertThat(fixBugSpringBoot21056(output)).contains(message);
    }

    private CharSequence fixBugSpringBoot21056(CapturedOutput output) {
        final String all = output.getAll();

        if (!all.contains("LOGBACK")) {
            return all;
        }

        return all.substring(all.indexOf('\n') + 1);
    }
}
