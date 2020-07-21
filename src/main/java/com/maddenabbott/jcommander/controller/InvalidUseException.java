package com.maddenabbott.jcommander.controller;

public class InvalidUseException extends RuntimeException {
    public InvalidUseException(String message, String usage, Throwable cause) {
        super(message + "\n" + usage, cause);
    }

    public InvalidUseException(String message, String usage) {
        super(message + "\n" + usage);
    }


}
