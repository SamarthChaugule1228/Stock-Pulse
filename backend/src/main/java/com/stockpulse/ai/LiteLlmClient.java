package com.stockpulse.ai;

import com.stockpulse.exception.AiServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class LiteLlmClient implements LlmClient {

	private final RestClient restClient;
	private final String apiKey;
	private final String model;
	private final String product;
	private final String cookie;

	public LiteLlmClient(RestClient.Builder builder,
						 @Value("${llm.base-url:}") String baseUrl,
						 @Value("${llm.api-key:}") String apiKey,
						 @Value("${llm.model:}") String model,
						 @Value("${llm.product:}") String product,
						 @Value("${llm.cookie:}") String cookie) {
		this.restClient = builder.baseUrl(baseUrl.isBlank() ? "http://localhost" : baseUrl).build();
		this.apiKey = apiKey;
		this.model = model;
		this.product = product;
		this.cookie = cookie;
	}

	@Override
	public String complete(String prompt) {
		if (apiKey.isBlank() || model.isBlank()) {
			throw new AiServiceException("LLM configuration is not available");
		}
		try {
			Map<String, Object> body = Map.of(
					"model", model,
					"temperature", 0.1,
					"messages", List.of(Map.of("role", "user", "content", prompt))
			);
			var request = restClient.post()
					.uri("/chat/completions")
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + apiKey)
					.body(body);
			if (!product.isBlank()) {
				request.header("product", product);
			}
			if (!cookie.isBlank()) {
				request.header("Cookie", cookie);
			}
			return request.retrieve()
					.body(String.class);
		} catch (Exception exception) {
			throw new AiServiceException("LLM request failed", exception);
		}
	}
}
