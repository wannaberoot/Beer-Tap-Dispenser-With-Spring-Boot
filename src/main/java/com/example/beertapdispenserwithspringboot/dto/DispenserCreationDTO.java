package com.example.beertapdispenserwithspringboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DispenserCreationDTO implements Serializable {

    private static final long serialVersionUID = 2348556418509736128L;

    @JsonProperty("id")
    private String id;
    @JsonProperty("flow_volume")
    private BigDecimal flowVolume;
}
