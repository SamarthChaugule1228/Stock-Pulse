package com.stockpulse.ai;

import com.stockpulse.exception.AiServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class LiteLlmClient implements LlmClient {
	private static final Logger log = LoggerFactory.getLogger(LiteLlmClient.class);

	private final RestClient restClient;
	private final String baseUrl;
	private final String apiKey;
	private final String model;
	private final String product;
	private final String cookie;

	@Autowired
	public LiteLlmClient(RestClient.Builder builder,
						 @Value("${llm.base-url:}") String baseUrl,
						 @Value("${llm.api-key:}") String apiKey,
						 @Value("${llm.model:qwen-cursor}") String model,
						 @Value("${llm.product:}") String product,
						 @Value("${llm.cookie:}") String cookie) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(Duration.ofSeconds(5));
		requestFactory.setReadTimeout(Duration.ofSeconds(30));
		String configuredBaseUrl = (baseUrl.isBlank() ? "http://localhost" : baseUrl).replaceAll("/+$", "");
		this.restClient = builder.requestFactory(requestFactory).baseUrl(configuredBaseUrl).build();
		this.baseUrl = configuredBaseUrl;
		this.apiKey = apiKey;
		this.model = model;
		this.product = product;
		this.cookie = cookie;
	}

	LiteLlmClient(RestClient restClient, String baseUrl, String apiKey, String model, String product, String cookie) {
		this.restClient = restClient;
		this.baseUrl = baseUrl;
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
			log.info("Calling LLM model={} endpoint={}/chat/completions", model, baseUrl);
			Map<String, Object> body = Map.of(
					"model", model,
					"temperature", 0.1,
					"messages", List.of(Map.of("role", "user", "content", prompt))
			);
			var request = restClient.post()
					.uri(baseUrl + "/chat/completions")
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
