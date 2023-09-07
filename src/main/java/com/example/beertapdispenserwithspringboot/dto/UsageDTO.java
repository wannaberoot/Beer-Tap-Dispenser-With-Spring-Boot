package com.example.beertapdispenserwithspringboot.dto;

import com.example.beertapdispenserwithspringboot.constants.DispenserConstants;
import com.example.beertapdispenserwithspringboot.model.Usage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class UsageDTO implements Serializable {

    private static final long serialVersionUID = 99708198531707807L;

    @JsonProperty("opened_at")
    private LocalDateTime openedAt;
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    @JsonProperty("flow_volume")
    private BigDecimal flowVolume;
    @JsonProperty("total_spent")
    private BigDecimal totalSpent;

    public static UsageDTO covert(final Usage usage) {
        final UsageDTO usageDTO = new UsageDTO();
        usageDTO.setOpenedAt((usage.getOpenedAt() != null) ? usage.getOpenedAt() : null);
        usageDTO.setClosedAt((usage.getClosedAt() != null) ? usage.getClosedAt() : null);
        usageDTO.setFlowVolume((usage.getFlowVolume() != null) ? new BigDecimal(usage.getFlowVolume()) : null);
        usageDTO.setTotalSpent((usage.getTotalSpent() != null) ? new BigDecimal(usage.getTotalSpent()) : calculateInstantAmount(usage.getOpenedAt(), new BigDecimal(usage.getFlowVolume())));
        return usageDTO;
    }

    private static BigDecimal calculateInstantAmount(final LocalDateTime openedAt, final BigDecimal flowVolume) {
        final LocalDateTime now = LocalDateTime.now();
        final Long flowDuration = Duration.between(openedAt, now).toSeconds();
        final BigDecimal instantVolume = flowVolume.multiply(new BigDecimal(flowDuration));
        final BigDecimal instantAmount = instantVolume.multiply(new BigDecimal(DispenserConstants.PRICE_PER_LITER));
        return instantAmount.setScale(4);
    }
}
