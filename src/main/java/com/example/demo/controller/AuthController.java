
package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository repo, PasswordEncoder encoder){this.repo=repo;this.encoder=encoder;}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String email = body.get("email");
        String password = body.get("password");
        User u = repo.findByEmail(email).orElse(null);
        if (u == null) return ResponseEntity.status(401).body(Map.of("error","invalid_credentials"));
        if (!encoder.matches(password, u.getPassword())) return ResponseEntity.status(401).body(Map.of("error","invalid_credentials"));
        String token = JwtUtil.generateToken(u.getEmail(), u.getRole());
        return ResponseEntity.ok(Map.of("accessToken", token, "tokenType", "Bearer"));
    }
}
