package com.stockpulse.commerce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommerceAdvisor {

	private final Map<String, CommerceStrategy> strategies = new ConcurrentHashMap<>();
	private final RuleBasedCommerceStrategy ruleStrategy;
	private volatile String activeStrategy;

	public CommerceAdvisor(RuleBasedCommerceStrategy ruleStrategy, AiCommerceStrategy aiStrategy,
						   @Value("${commerce.strategy:RULE}") String configuredStrategy) {
		this.ruleStrategy = ruleStrategy;
		strategies.put("RULE", ruleStrategy);
		strategies.put("AI", aiStrategy);
		this.activeStrategy = configuredStrategy.toUpperCase();
		if (!strategies.containsKey(this.activeStrategy)) {
			this.activeStrategy = "RULE";
		}
	}

	public CommerceRecommendation recommend(CommerceContext context) {
		return strategies.get(activeStrategy).recommend(context);
	}

	public CommerceRecommendation ruleRecommendation(CommerceContext context) {
		return ruleStrategy.recommend(context);
	}

	public String getActiveStrategy() {
		return activeStrategy;
	}

	public void setActiveStrategy(String strategyName) {
		String normalized = strategyName.toUpperCase();
		if (!strategies.containsKey(normalized)) {
			throw new IllegalArgumentException("Unsupported commerce strategy: " + strategyName);
		}
		activeStrategy = normalized;
	}
}
