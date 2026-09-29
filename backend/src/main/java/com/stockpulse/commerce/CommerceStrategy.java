package com.stockpulse.commerce;

public interface CommerceStrategy {
	CommerceRecommendation recommend(CommerceContext context);
}
