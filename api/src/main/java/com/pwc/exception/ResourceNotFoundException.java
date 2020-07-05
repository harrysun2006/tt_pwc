package com.pwc.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Object value) {
        super("Resource not found: " + value);
    }

    public ResourceNotFoundException() {
        super("Resource not found!");
    }

}


