package com.stockpulse.dto.product;

import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;

import java.math.BigDecimal;

public record ProductResponse(
	Long id,
	String sku,
	String name,
	Category category,
	BigDecimal currentPrice,
	int stockLevel,
	int reorderThreshold,
	int demandVelocity,
	ProductLifecycle lifecycle
) {
}
