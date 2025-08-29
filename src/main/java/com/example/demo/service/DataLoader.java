
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    public DataLoader(UserRepository repo, PasswordEncoder encoder){this.repo=repo;this.encoder=encoder;}
    @Override
    public void run(String... args) throws Exception {
        if (repo.count() == 0) {
            User u = new User("Admin","admin@example.com", encoder.encode("admin123"), "ROLE_ADMIN");
            repo.save(u);
        }
    }
}
