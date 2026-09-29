package com.stockpulse.commerce;

import com.stockpulse.enums.Category;
import com.stockpulse.enums.PricingDirection;
import com.stockpulse.enums.TriggerReason;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RuleBasedCommerceStrategyTest {

    private final RuleBasedCommerceStrategy strategy = new RuleBasedCommerceStrategy();

    @Test
    void raisesPriceForLowInventory() {
        CommerceRecommendation result = strategy.recommend(context(8, 15, 12, 10));

        assertThat(result.recommendedPrice()).isEqualByComparingTo("27.49");
        assertThat(result.pricingDirection()).isEqualTo(PricingDirection.INCREASE);
        assertThat(result.recommendedQuantity()).isEqualTo(37);
    }

    @Test
    void raisesPriceForDemandSpikeWhenInventoryIsHealthy() {
        CommerceRecommendation result = strategy.recommend(context(30, 10, 15, 5));

        assertThat(result.recommendedPrice()).isEqualByComparingTo("26.24");
        assertThat(result.pricingDirection()).isEqualTo(PricingDirection.INCREASE);
    }

    @Test
    void holdsPriceOtherwise() {
        CommerceRecommendation result = strategy.recommend(context(30, 10, 5, 5));

        assertThat(result.pricingDirection()).isEqualTo(PricingDirection.HOLD);
        assertThat(result.recommendedQuantity()).isEqualTo(1);
    }

    private CommerceContext context(int stock, int threshold, int demand, double average) {
        return new CommerceContext(1L, "Test", Category.APPAREL, new BigDecimal("24.99"), stock,
                threshold, demand, average, TriggerReason.MANUAL);
    }
}