package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service  // <- Isso faz o Spring reconhecer como um Bean
@RequiredArgsConstructor  // <- Lombok gera construtor com dependências
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco pelo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // 2. Converte seu User para o UserDetails do Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())           // Username será o email
                .password(user.getPassword())        // Senha já criptografada
                .authorities(Collections.singletonList(
                    new SimpleGrantedAuthority(user.getRole())  // Ex: "ROLE_ADMIN"
                ))
                .build();
    }
}