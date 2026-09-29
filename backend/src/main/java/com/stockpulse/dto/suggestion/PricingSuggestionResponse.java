package com.stockpulse.dto.suggestion;

import com.stockpulse.enums.PricingDirection;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;

import java.math.BigDecimal;
import java.time.Instant;

public record PricingSuggestionResponse(
	Long id,
	Long productId,
	BigDecimal currentPrice,
	BigDecimal recommendedPrice,
	PricingDirection direction,
	double confidence,
	String reasoning,
	SuggestionStatus status,
	TriggerReason triggerReason,
	Instant createdAt
) {
}
