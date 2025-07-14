package com.example.demo.controller;
import com.example.demo.model.Book;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

   private final BookService bookService;
   private final CartService cartService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal User currentUser) {
        List<Book> books = bookService.getAllBooks();
        books.forEach(book -> {
            if (book.getQuantity() == 0) {
                book.setAvailabilityStatus("Товар закончился");
            } else if (book.getQuantity() < 5) {
                book.setAvailabilityStatus("Осталось мало");
            }
        });
        model.addAttribute("books", books);
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userRole", currentUser.getRole().name());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "home";
    }

}