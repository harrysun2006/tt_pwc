package com.pwc.exception;

public class UnhandledCaseException extends RuntimeException {

    public UnhandledCaseException(Object value) {
        super("Unhandled case: " + value);
    }

    public UnhandledCaseException() {
        super("Unhandled case");
    }

}


