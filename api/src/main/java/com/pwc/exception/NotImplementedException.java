package com.pwc.exception;

public class NotImplementedException extends ApplicationException {

    private static final long serialVersionUID = 5406331742859946081L;

    public NotImplementedException() {
        this("Not implemented yet!");
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
