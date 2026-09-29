package com.stockpulse.ai;

import com.stockpulse.commerce.CommerceContext;
import com.stockpulse.commerce.CommerceRecommendation;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.PricingDirection;
import com.stockpulse.enums.TriggerReason;
import com.stockpulse.exception.AiServiceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiRecommendationValidatorTest {

    @Test
    void rejectsExtremePrice() {
        CommerceContext context = new CommerceContext(1L, "Test", Category.HOME, new BigDecimal("10.00"),
                10, 5, 2, 2, TriggerReason.DEMAND_SPIKE);
        CommerceRecommendation recommendation = new CommerceRecommendation(new BigDecimal("1000"),
                PricingDirection.INCREASE, 0.9, "reason", 2, 7, 0.9, "reason");

        assertThatThrownBy(() -> new AiRecommendationValidator().validate(recommendation, context))
                .isInstanceOf(AiServiceException.class);
    }
}