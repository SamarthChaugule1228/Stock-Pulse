package com.stockpulse.dto.suggestion;

import com.stockpulse.enums.SuggestionStatus;
import jakarta.validation.constraints.NotNull;

public record SuggestionDecisionRequest(@NotNull SuggestionStatus status) {
}
