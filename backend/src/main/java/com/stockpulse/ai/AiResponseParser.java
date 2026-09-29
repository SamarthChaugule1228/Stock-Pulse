package com.stockpulse.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpulse.commerce.CommerceRecommendation;
import com.stockpulse.enums.PricingDirection;
import com.stockpulse.exception.AiServiceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AiResponseParser {

	private final ObjectMapper objectMapper;

	public AiResponseParser(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public CommerceRecommendation parse(String response) {
		try {
			JsonNode root = objectMapper.readTree(response);
			JsonNode content = root.path("choices").path(0).path("message").path("content");
			String json = content.isTextual() ? content.asText() : response;
			JsonNode recommendation = objectMapper.readTree(json);
			return new CommerceRecommendation(
					new BigDecimal(required(recommendation, "recommendedPrice").asText()),
					PricingDirection.valueOf(required(recommendation, "direction").asText().toUpperCase()),
					required(recommendation, "pricingConfidence").asDouble(),
					required(recommendation, "pricingReasoning").asText(),
					required(recommendation, "recommendedQuantity").asInt(),
					recommendation.path("suggestedLeadTimeDays").asInt(7),
					required(recommendation, "reorderConfidence").asDouble(),
					required(recommendation, "reorderReasoning").asText()
			);
		} catch (Exception exception) {
			throw new AiServiceException("Malformed AI response", exception);
		}
	}

	private JsonNode required(JsonNode node, String field) {
		JsonNode value = node.get(field);
		if (value == null || value.isNull()) {
			throw new AiServiceException("AI response is missing " + field);
		}
		return value;
	}
}
