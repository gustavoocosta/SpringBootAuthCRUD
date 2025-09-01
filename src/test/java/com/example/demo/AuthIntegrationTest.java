package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    private final RestTemplate rest = new RestTemplate();

    @Test
    void loginShouldReturnToken() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "user");
        payload.put("password", "password");

        ResponseEntity<Map> response =
                rest.postForEntity("http://localhost:8080/auth/login", payload, Map.class);

        Map<?, ?> body = Objects.requireNonNull(response.getBody(), "response body is null");
        Object token = body.get("token");
        assertThat(token).isInstanceOf(String.class);
        assertThat(((String) token)).isNotBlank();
    }
}
