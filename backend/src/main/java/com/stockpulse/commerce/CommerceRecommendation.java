package com.stockpulse.commerce;

import com.stockpulse.enums.PricingDirection;

import java.math.BigDecimal;

public record CommerceRecommendation(
	BigDecimal recommendedPrice,
	PricingDirection pricingDirection,
	double pricingConfidence,
	String pricingReasoning,
	int recommendedQuantity,
	int suggestedLeadTimeDays,
	double reorderConfidence,
	String reorderReasoning
) {
}
