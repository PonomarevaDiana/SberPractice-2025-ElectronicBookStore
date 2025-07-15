package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.model.CartItem;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddToCart() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        Book book = new Book();
        book.setTitle("Книга");
        book.setAuthor("Автор");
        book.setPrice(BigDecimal.valueOf(300));
        book.setQuantity(3);
        Book savedBook = bookRepository.save(book);

        cartService.addToCart(savedUser.getId(), savedBook.getId(), 1);

        List<CartItem> items = cartService.getCartItems(savedUser);
        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getQuantity());
        assertEquals(savedBook.getId(), items.get(0).getBook().getId());
    }
}
