package com.pwc.exception;

public class AuthorisationException extends ApplicationException {

    private static final long serialVersionUID = -7712533868827884440L;

    public AuthorisationException() {
        this("Invalid authorisation!");
    }

    public AuthorisationException(String message) {
        super(message);
    }

    public AuthorisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorisationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
