package com.example.beertapdispenserwithspringboot.constants;

public enum MSExceptionEnum {

    DISPENSER_NOT_FOUND("MS-001", "Dispenser not found!"),
    DISPENSER_STATUS_NOT_VALID("MS-002", "Dispenser status is already opened/closed!"),
    INVALID_INPUT("MS-006", "Invalid input!");

    private String errorCode;
    private String errorMessage;

    MSExceptionEnum(final String errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
