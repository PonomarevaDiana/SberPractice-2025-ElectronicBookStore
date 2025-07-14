package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserRegistrationDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/register")
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result, Model model) {

        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        if (result.hasErrors()) {
            return "register";
        }

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
            return "register";
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            result.rejectValue("email", "error.user", "Email уже используется");
            return "register";
        }

        if (userService.existsByUsername(registrationDto.getUsername())) {
            result.rejectValue("username", "error.user", "Имя пользователя уже занято");
            return "register";
        }

        User.Role role = User.Role.CUSTOMER;
        if (registrationDto.getUserType().equals("employee")) {
            role = User.Role.EMPLOYEE;
        } else if (registrationDto.getUserType().equals("admin")) {
            role = User.Role.ADMIN;
        }

        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(registrationDto.getPhoneNumber())
                .role(role)
                .build();
        userService.save(user);
        return "redirect:/login?registered";
    }
}