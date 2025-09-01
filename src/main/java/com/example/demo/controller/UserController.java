package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User u) {
        // Check if email already exists
        if (repo.findByEmail(u.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists"));
        }
        
        // Encrypt password if provided
        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(u.getPassword()));
        }
        
        // Set default role if not provided
        if (u.getRole() == null || u.getRole().isEmpty()) {
            u.setRole("ROLE_USER");
        }
        
        return ResponseEntity.ok(repo.save(u));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User u) {
        return repo.findById(id).map(existing -> {
            if (u.getName() != null) existing.setName(u.getName());
            if (u.getEmail() != null) existing.setEmail(u.getEmail());
            if (u.getRole() != null) existing.setRole(u.getRole());
            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(u.getPassword()));
            }
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repo.findById(id).map(existing -> {
            repo.delete(existing);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
