package com.stockpulse.dto.suggestion;

import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;

import java.time.Instant;

public record ReorderSuggestionResponse(
	Long id,
	Long productId,
	int currentStock,
	int recommendedQuantity,
	int suggestedLeadTimeDays,
	double confidence,
	String reasoning,
	SuggestionStatus status,
	TriggerReason triggerReason,
	Instant createdAt
) {
}
