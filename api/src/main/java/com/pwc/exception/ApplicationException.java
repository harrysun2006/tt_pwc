package com.pwc.exception;

public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = -2472055982901328166L;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String format, Object ... args) {
        super(String.format(format, args));
    }

    public ApplicationException(String format, Throwable cause, Object ... args) {
        super(String.format(format, args), cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
