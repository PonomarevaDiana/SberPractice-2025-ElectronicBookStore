package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/shopping-history")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    @GetMapping
    public String viewOrders(@AuthenticationPrincipal User user,
                             Model model) {
        try {
            List<Order> orders = orderService.getUserOrdersSorted(user.getId());
            model.addAttribute("orders", orders);
            model.addAttribute("currentUser", user);
            model.addAttribute("userRole", user.getRole().name());
            model.addAttribute("isAuthenticated", true);
            return "history";
        } catch (Exception e) {
            return "history";
        }
    }

    @GetMapping("/{orderId}")
    @ResponseBody
    public String getOrderDetails(@AuthenticationPrincipal User user,
                                  @PathVariable Long orderId,
                                  Model model) {
        try {
            if (user == null  ||  user.getRole() != User.Role.CUSTOMER) {
                return "redirect:/login";
            }

            Order order = orderService.getOrderWithItems(orderId);
            if (!order.getUser().getId().equals(user.getId())) {
                return "redirect:/shopping-history";
            }

            model.addAttribute("order", order);
            return "order-details";

        } catch (Exception e) {
            return "redirect:/shopping-history";
        }
    }

}