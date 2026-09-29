package com.stockpulse.ai;

import com.stockpulse.enums.PricingDirection;

import java.math.BigDecimal;

public record AiRecommendationPayload(
        BigDecimal recommendedPrice,
        PricingDirection direction,
        double pricingConfidence,
        String pricingReasoning,
        int recommendedQuantity,
        int suggestedLeadTimeDays,
        double reorderConfidence,
        String reorderReasoning
) {
}