package com.example.beertapdispenserwithspringboot.exception;

public class MSException extends Exception {

    private static final long serialVersionUID = 1016258813409241274L;

    private final String resource;
    private final String errorCode;

    public MSException(final String errorCode, final String errorMessage, final String resource) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
