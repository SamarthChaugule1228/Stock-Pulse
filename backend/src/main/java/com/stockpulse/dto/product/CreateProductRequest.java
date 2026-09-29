package com.stockpulse.dto.product;

import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
	@NotBlank String sku,
	@NotBlank String name,
	@NotNull Category category,
	@NotNull @DecimalMin(value = "0.01") BigDecimal currentPrice,
	@Min(0) int stockLevel,
	@Min(1) int reorderThreshold,
	@Min(0) int demandVelocity,
	ProductLifecycle lifecycle
) {
}
