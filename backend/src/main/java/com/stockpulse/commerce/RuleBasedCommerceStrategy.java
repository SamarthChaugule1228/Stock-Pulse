package com.stockpulse.commerce;

import com.stockpulse.enums.PricingDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedCommerceStrategy implements CommerceStrategy {

	@Override
	public CommerceRecommendation recommend(CommerceContext context) {
		BigDecimal recommendedPrice = context.currentPrice();
		PricingDirection direction = PricingDirection.HOLD;
		String pricingReasoning = "No low-inventory or demand-spike rule was triggered; hold the current price.";
		double pricingConfidence = 0.75;

		if (context.stockLevel() < context.reorderThreshold()) {
			recommendedPrice = context.currentPrice().multiply(BigDecimal.valueOf(1.10))
					.setScale(2, RoundingMode.HALF_UP);
			direction = PricingDirection.INCREASE;
			pricingConfidence = 0.90;
			pricingReasoning = "Inventory is below the reorder threshold, so the baseline rule recommends a 10% price increase to protect scarce stock.";
		} else if (context.demandVelocity() > 2 * context.categoryAverageDemandVelocity()) {
			recommendedPrice = context.currentPrice().multiply(BigDecimal.valueOf(1.05))
					.setScale(2, RoundingMode.HALF_UP);
			direction = PricingDirection.INCREASE;
			pricingConfidence = 0.85;
			pricingReasoning = "Demand velocity is more than twice the category average, so the baseline rule recommends a 5% price increase for the spike.";
		}

		int quantity = Math.max(1, context.reorderThreshold() * 3 - context.stockLevel());
		String reorderReasoning = "Reorder quantity uses three times the threshold minus current stock, with a minimum quantity of one.";
		return new CommerceRecommendation(recommendedPrice, direction, pricingConfidence, pricingReasoning,
				quantity, 7, 0.90, reorderReasoning);
	}
}
