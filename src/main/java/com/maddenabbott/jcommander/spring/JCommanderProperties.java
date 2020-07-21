package com.maddenabbott.jcommander.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jcommander")
@Validated
public class JCommanderProperties {

    /**
     * Output uncaught exceptions to standard error.
     */
    private boolean outputErrors;

    public boolean isOutputErrors() {
        return outputErrors;
    }

    public void setOutputErrors(boolean outputErrors) {
        this.outputErrors = outputErrors;
    }
}
