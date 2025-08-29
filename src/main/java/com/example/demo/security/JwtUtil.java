
package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key key = Keys.hmacShaKeyFor(System.getProperty("jwt.secret", "default-secret-key-please-change").getBytes());
    private static final long EXP_MS = 1000L * 60 * 60 * 4;

    public static String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(key)
                .compact();
    }

    public static Jws<Claims> validate(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
