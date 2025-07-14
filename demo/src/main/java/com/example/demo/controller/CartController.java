package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BookService;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
   private final CartService cartService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final OrderService orderService;

    @GetMapping
    public String viewCart(Model model,
                           @AuthenticationPrincipal User currentUser,
                           RedirectAttributes redirectAttributes) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(currentUser.getId());
            BigDecimal total = cartService.calculateTotal(currentUser.getId());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userRole", currentUser.getRole().name());
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("cartItems", cartItems != null ? cartItems : Collections.emptyList());
            model.addAttribute("totalPrice", total);

            return "cart";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PostMapping("/add")
    public String addToCart(
            @AuthenticationPrincipal User user,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") int quantity, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.getBookById(bookId)
                    .orElseThrow(() -> new RuntimeException("Книга не найдена"));
            if (book.getQuantity() < quantity) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Недостаточно книг на складе. Доступно: " + book.getQuantity());
                return "redirect:/book/" + bookId;
            }
            redirectAttributes.addFlashAttribute("successMessage", "Товар добавлен в корзину");

            cartService.addToCart(user.getId(), bookId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/book/" + bookId;
    }

    @PostMapping("/update/{id}")
    public String updateQuantity(
            @PathVariable Long id,
            @RequestParam String action,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {

        if (!cartService.isItemBelongsToUser(id, user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Нет доступа к этому товару");
            return "redirect:/cart";
        }

        try {
            CartItem item = cartService.findById(id);
            Book book = item.getBook();
            int oldQuantity = item.getQuantity();

            if ("increase".equals(action)) {
                if (book.getQuantity() < 1) {
                    redirectAttributes.addFlashAttribute("error", "Недостаточно товара на складе");
                    return "redirect:/cart";
                }
                item.setQuantity(oldQuantity + 1);
            } else if ("decrease".equals(action)) {
                item.setQuantity(Math.max(1, oldQuantity - 1));
            }

            cartService.save(item);
            redirectAttributes.addFlashAttribute("success", "Количество обновлено");
        } catch (Exception e) {
            log.error("Error updating quantity", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении количества");
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {

        if (!cartService.isItemBelongsToUser(id, user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Нет прав для удаления");
            return "redirect:/cart";
        }

        try {
            cartService.removeItem(id);
        } catch (Exception e) {
            log.error("Error removing from cart", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении товара");
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            for (CartItem item : cartItems) {
                Book book = item.getBook();
                if (book.getQuantity() < item.getQuantity()) {
                    redirectAttributes.addFlashAttribute("error",
                            "Недостаточно товара на складе");
                    return "redirect:/cart";
                }
            }

            for (CartItem item : cartItems) {
                Book book = item.getBook();
                book.setQuantity(book.getQuantity() - item.getQuantity());
                bookService.saveBook(book);
            }

            try {
                if (user == null) {
                    return "redirect:/login";
                }

                Order order = orderService.createOrderFromCart(user.getId());
                cartService.clearCart(user.getId());

                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Заказ #%d успешно оформлен!", order.getId()));

                return "redirect:/shopping-history";

            } catch (IllegalStateException e) {
                return "redirect:/cart";

            } catch (Exception e) {
                return "redirect:/cart";
            }

        } catch (Exception e) {
            log.error("Checkout error", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при оформлении заказа: " + e.getMessage());
        }
        return "redirect:/cart";
    }
}
