package com.example.demo.model;

import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Admin {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdmin() {
        String adminEmail = "admin@example.com";
        String adminUsername = "admin";
        String adminPassword = "admin123";

        if (!userRepository.existsByEmail(adminEmail) && !userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .phoneNumber("+89115337361")
                    .password(passwordEncoder.encode(adminPassword))
                    .role(User.Role.ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}
