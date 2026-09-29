package com.stockpulse.dto.product;

import jakarta.validation.constraints.Min;

public record UpdateStockRequest(@Min(0) int stockLevel) {
}
