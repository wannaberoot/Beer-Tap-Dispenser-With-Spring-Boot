package com.example.beertapdispenserwithspringboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorDTO implements Serializable {

    private static final long serialVersionUID = 1704330456587768078L;

    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("resource")
    private String resource;
}
