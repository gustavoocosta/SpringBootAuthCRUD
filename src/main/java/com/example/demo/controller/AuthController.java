package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        Optional<User> userOpt = userRepository.findByEmail(loginData.get("email"));

        if (userOpt.isPresent() && passwordEncoder.matches(loginData.get("password"), userOpt.get().getPassword())) {
            String token = jwtUtil.generateToken(userOpt.get().getEmail(), userOpt.get().getRole());
            return Map.of(
                    "accessToken", token,
                    "tokenType", "Bearer",
                    "role", userOpt.get().getRole()
            );
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
