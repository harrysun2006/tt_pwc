package com.pwc.exception;

public class BadRequestException extends ApplicationException {

    private static final long serialVersionUID = -8906437161377078744L;

    public BadRequestException() {
        this("Bad request! Invalid headers or payload!");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public BadRequestException(String message, Object... parameters) {
        super(String.format(message, parameters));
    }
}
