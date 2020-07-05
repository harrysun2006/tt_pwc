package com.pwc.exception;

public class ConflictException extends ApplicationException {

    private static final long serialVersionUID = -3296505224445884016L;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
