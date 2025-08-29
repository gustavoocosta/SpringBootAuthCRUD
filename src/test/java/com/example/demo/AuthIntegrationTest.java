
package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {
    @Autowired private TestRestTemplate rest;

    @Test
    void adminCanAccessSecret() {
        Map login = Map.of("email","admin@example.com","password","admin123");
        ResponseEntity<Map> resp = rest.postForEntity("/auth/login", login, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = (String)resp.getBody().get("accessToken");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<Map> secretResp = rest.exchange("/api/admin/secret", HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        assertThat(secretResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secretResp.getBody().get("secret")).isEqualTo("only-admins-can-see-this");
    }

    @Test
    void userCannotAccessSecret() {
        Map login = Map.of("email","user@example.com","password","user123");
        ResponseEntity<Map> resp = rest.postForEntity("/auth/login", login, Map.class);
        String token = (String)resp.getBody().get("accessToken");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<Map> secretResp = rest.exchange("/api/admin/secret", HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        assertThat(secretResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
