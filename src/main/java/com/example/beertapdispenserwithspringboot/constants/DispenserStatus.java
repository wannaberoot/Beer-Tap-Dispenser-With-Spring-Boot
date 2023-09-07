package com.example.beertapdispenserwithspringboot.constants;

public enum DispenserStatus {

    OPEN("open"),
    CLOSE("close");

    private String value;

    DispenserStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
