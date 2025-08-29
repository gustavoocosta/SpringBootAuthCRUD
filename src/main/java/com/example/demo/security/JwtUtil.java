
package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.nio.charset.StandardCharsets;

public class JwtUtil {
    private static Key key() {
        String secret = System.getenv().getOrDefault("JWT_SECRET", System.getProperty("jwt.secret", "default-secret-key-please-change"));
        byte[] b = secret.getBytes(StandardCharsets.UTF_8);
        // ensure key length; Keys.hmacShaKeyFor requires sufficient length
        return Keys.hmacShaKeyFor(b);
    }
    private static final long EXP_MS = 1000L * 60 * 60 * 4;

    public static String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(key())
                .compact();
    }

    public static Jws<Claims> validate(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }
}
