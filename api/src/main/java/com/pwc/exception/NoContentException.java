package com.pwc.exception;

public class NoContentException extends ApplicationException {

    private static final long serialVersionUID = -5889884090171454623L;

    public NoContentException(String message) {
        super(message);
    }

    public NoContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoContentException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
