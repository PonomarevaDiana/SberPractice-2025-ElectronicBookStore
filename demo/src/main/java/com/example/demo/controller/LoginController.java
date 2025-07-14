package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "denied", required = false) String denied,
            @RequestParam(name = "auth_required", required = false) String authRequired,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль");
        }

        if (denied != null) {
            model.addAttribute("errorMessage", "Недостаточно прав доступа");
        }

        if (authRequired != null) {
            model.addAttribute("errorMessage", "Требуется авторизация для доступа к этой странице");
        }

        return "login";
    }
}
