package com.stockpulse.dto.order;

import jakarta.validation.constraints.Min;

public record OrderRequest(@Min(1) int quantity) {
}
