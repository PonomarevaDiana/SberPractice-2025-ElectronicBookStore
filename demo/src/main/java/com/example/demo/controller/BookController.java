package com.example.demo.controller;
import com.example.demo.model.Book;
import com.example.demo.model.CartItem;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;
import com.example.demo.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/book")
@Controller
public class BookController  implements WebMvcConfigurer {
    private final BookService bookService;
    private final CartService cartService;
    private final UserService userService;
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/book/{id}").setViewName("book");
    }
    @GetMapping("/{id}")
    public String showBookDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new NotFoundException("Книга не найдена с id: " + id));
        boolean inCart = false;
        int cartQuantity = 0;

        if (currentUser !=null){
            inCart = cartService.getQuantityInCart(currentUser.getId(), id) > 0;
            cartQuantity = inCart ? cartService.getQuantityInCart(currentUser.getId(), id) : 0;
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userRole", currentUser.getRole().name());
            model.addAttribute("isAuthenticated", true); // Флаг аутентификации
        }else {
        model.addAttribute("isAuthenticated", false);
    }
        model.addAttribute("book", book);
        model.addAttribute("inCart", inCart);
        model.addAttribute("cartQuantity", cartQuantity);

        return "book";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @PathVariable Long id,
            @RequestParam String action, @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Требуется авторизация");
            return "redirect:/login";
        }

        try {
            CartItem item = cartService.findById(id);
            if (item.getUser().getId().equals(currentUser.getId())) {
                if ("increase".equals(action)) {
                    item.setQuantity(item.getQuantity() + 1);
                } else if ("decrease".equals(action)) {
                    item.setQuantity(Math.max(1, item.getQuantity() - 1));
                }
                cartService.save(item);
            } else {
                redirectAttributes.addFlashAttribute("error", "Нет прав на изменение");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления");
        }

        return "redirect:/cart";
    }
}
