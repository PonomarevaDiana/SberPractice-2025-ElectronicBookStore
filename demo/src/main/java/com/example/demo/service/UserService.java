package com.example.demo.service;

import com.example.demo.exceptions.EmailAlreadyExistsException;
import com.example.demo.exceptions.UsernameAlreadyExistsException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new UsernameAlreadyExistsException("Username already exists");
                });
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new EmailAlreadyExistsException("Email already exists");
                });
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
