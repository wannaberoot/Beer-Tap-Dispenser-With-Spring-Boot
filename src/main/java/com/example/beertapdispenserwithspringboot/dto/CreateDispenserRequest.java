package com.example.beertapdispenserwithspringboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreateDispenserRequest implements Serializable {

    private static final long serialVersionUID = 1164887340254524257L;

    @JsonProperty("flow_volume")
    private BigDecimal flowVolume;
}
