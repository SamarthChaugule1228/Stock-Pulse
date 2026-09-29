package com.stockpulse.ai;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LiteLlmClientTest {

    @Test
    void sendsCompletionToConfiguredV1EndpointWithConfiguredHeaders() {
        String baseUrl = "https://litellm.example.test/v1";
        RestClient.Builder builder = RestClient.builder().baseUrl(baseUrl);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(baseUrl + "/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-only-token"))
                .andExpect(header("product", "PC1"))
                .andExpect(header("Cookie", "session=test-only"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"model\":\"qwen-cursor\"")))
                .andRespond(withSuccess("{\"choices\":[{\"message\":{\"content\":\"{}\"}}]}", MediaType.APPLICATION_JSON));

        LiteLlmClient client = new LiteLlmClient(builder.build(), baseUrl,
                "test-only-token", "qwen-cursor", "PC1", "session=test-only");

        assertTrue(client.complete("test prompt").contains("choices"));
        server.verify();
    }
}
