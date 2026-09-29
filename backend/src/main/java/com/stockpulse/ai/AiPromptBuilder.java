package com.stockpulse.ai;

import com.stockpulse.commerce.CommerceContext;
import com.stockpulse.enums.TriggerReason;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AiPromptBuilder {

    public String build(CommerceContext context) {
	String shared = String.format(Locale.ROOT,
		"Product: %s; category: %s; current price: %s; stock: %d; reorder threshold: %d; demand velocity: %d; category average demand: %.2f. ",
		context.productName(), context.category(), context.currentPrice(), context.stockLevel(),
		context.reorderThreshold(), context.demandVelocity(), context.categoryAverageDemandVelocity());
	String intent = context.triggerReason() == TriggerReason.INVENTORY_LOW
		? "Inventory is below or approaching its reorder threshold. Evaluate scarcity, limited remaining inventory, demand context, whether a reasonable price increase protects stock or clearance is justified, and replenishment tradeoffs."
		: context.triggerReason() == TriggerReason.DEMAND_SPIKE
		? "Demand velocity has increased significantly relative to category peers. Evaluate whether the product is trending, keep any price change reasonable, and account for replenishment during the spike."
		: "This is a manual commerce review. Balance inventory, demand, pricing, and replenishment conservatively.";
	return shared + intent + " Trigger: " + context.triggerReason()
		+ ". Return JSON only with recommendedPrice, direction (INCREASE, DECREASE, or HOLD), pricingConfidence, pricingReasoning, recommendedQuantity, suggestedLeadTimeDays, reorderConfidence, and reorderReasoning.";
    }
}
