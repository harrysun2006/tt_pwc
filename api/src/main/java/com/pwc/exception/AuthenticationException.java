package com.pwc.exception;

public class AuthenticationException extends ApplicationException {

    private static final long serialVersionUID = -5529088520460270098L;

    public AuthenticationException() {
        this("Invalid authentication!");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
