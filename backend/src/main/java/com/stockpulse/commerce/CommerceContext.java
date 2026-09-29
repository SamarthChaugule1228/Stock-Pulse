package com.stockpulse.commerce;

import com.stockpulse.enums.Category;
import com.stockpulse.enums.TriggerReason;

import java.math.BigDecimal;

public record CommerceContext(
        Long productId,
        String productName,
        Category category,
        BigDecimal currentPrice,
        int stockLevel,
        int reorderThreshold,
        int demandVelocity,
        double categoryAverageDemandVelocity,
        TriggerReason triggerReason
) {
}