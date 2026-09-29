package com.stockpulse.commerce;

import com.stockpulse.ai.AiPromptBuilder;
import com.stockpulse.ai.AiRecommendationValidator;
import com.stockpulse.ai.AiResponseParser;
import com.stockpulse.ai.LlmClient;
import org.springframework.stereotype.Component;

@Component
public class AiCommerceStrategy implements CommerceStrategy {

	private final LlmClient llmClient;
	private final AiPromptBuilder promptBuilder;
	private final AiResponseParser responseParser;
	private final AiRecommendationValidator validator;

	public AiCommerceStrategy(LlmClient llmClient, AiPromptBuilder promptBuilder,
							  AiResponseParser responseParser, AiRecommendationValidator validator) {
		this.llmClient = llmClient;
		this.promptBuilder = promptBuilder;
		this.responseParser = responseParser;
		this.validator = validator;
	}

	@Override
	public CommerceRecommendation recommend(CommerceContext context) {
		String prompt = promptBuilder.build(context);
		CommerceRecommendation recommendation = responseParser.parse(llmClient.complete(prompt));
		validator.validate(recommendation, context);
		return recommendation;
	}
}
