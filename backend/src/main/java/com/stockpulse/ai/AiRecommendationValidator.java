package com.stockpulse.ai;

import com.stockpulse.commerce.CommerceContext;
import com.stockpulse.commerce.CommerceRecommendation;
import com.stockpulse.exception.AiServiceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AiRecommendationValidator {

	public void validate(CommerceRecommendation recommendation, CommerceContext context) {
		BigDecimal price = recommendation.recommendedPrice();
		if (price == null || price.compareTo(BigDecimal.ZERO) <= 0
				|| price.compareTo(context.currentPrice().multiply(BigDecimal.valueOf(3))) > 0
				|| price.compareTo(context.currentPrice().divide(BigDecimal.valueOf(3), 4, java.math.RoundingMode.HALF_UP)) < 0) {
			throw new AiServiceException("AI recommended price is outside sane bounds");
		}
		if (recommendation.recommendedQuantity() < 1 || recommendation.suggestedLeadTimeDays() < 1) {
			throw new AiServiceException("AI recommended quantity or lead time is invalid");
		}
		if (!validConfidence(recommendation.pricingConfidence()) || !validConfidence(recommendation.reorderConfidence())) {
			throw new AiServiceException("AI confidence must be between zero and one");
		}
		if (recommendation.pricingReasoning() == null || recommendation.pricingReasoning().isBlank()
				|| recommendation.reorderReasoning() == null || recommendation.reorderReasoning().isBlank()) {
			throw new AiServiceException("AI reasoning is required");
		}
	}

	private boolean validConfidence(double confidence) {
		return !Double.isNaN(confidence) && confidence >= 0 && confidence <= 1;
	}
}
