package com.example.beertapdispenserwithspringboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DispenserDTO implements Serializable {

    private static final long serialVersionUID = 268205493757205854L;

    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("usages")
    private List<UsageDTO> usages;
}
