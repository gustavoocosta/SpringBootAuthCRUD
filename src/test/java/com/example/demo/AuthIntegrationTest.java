package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate rest = new RestTemplate();

    @Test
    void loginShouldReturnToken() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "admin@example.com");  // Corrigido: era "username"
        payload.put("password", "admin123");        // Corrigido: era "password"

        String url = "http://localhost:" + port + "/auth/login";
        ResponseEntity<Map> response = rest.postForEntity(url, payload, Map.class);

        Map<?, ?> body = Objects.requireNonNull(response.getBody(), "response body is null");
        Object token = body.get("accessToken");  // Corrigido: era "token"
        assertThat(token).isInstanceOf(String.class);
        assertThat(((String) token)).isNotBlank();
    }
}