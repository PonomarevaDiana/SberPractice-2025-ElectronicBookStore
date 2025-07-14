package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/clients")
    public String clientsList(@RequestParam(required = false) String search, @AuthenticationPrincipal User currentUser, Model model) {
        List<User> clients;

        if (search != null && !search.isEmpty()) {
            clients = userRepository.findByRoleAndSearch(User.Role.CUSTOMER, search);
        } else {
            clients = userRepository.findByRole(User.Role.CUSTOMER);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("clients", clients);
        return "clients";
    }

    @GetMapping("/employees")
    public String employeesList(@RequestParam(required = false) String search, @AuthenticationPrincipal User currentUser, Model model) {
        List<User> employees;
        if (search != null && !search.isEmpty()) {
            employees = userRepository.findByRoleAndSearch(User.Role.EMPLOYEE, search);
        } else {
            employees = userRepository.findByRole(User.Role.EMPLOYEE);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", currentUser.getRole().name());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("employees", employees);
        return "employees";
    }

    @GetMapping("/employees/add-employee")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new User());
        return "add-employee";
    }

    @PostMapping("/employees/add-employee")
    public String addEmployee(@ModelAttribute User employee) {
        employee.setRole(User.Role.EMPLOYEE);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        userRepository.save(employee);
        return "redirect:/admin/employees";
    }
}