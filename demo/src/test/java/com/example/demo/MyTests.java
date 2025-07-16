package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OnlineBookStoreApplicationTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private static Long testBookId;
    private static Long testUserId;
    private static Long testOrderId;


    @Test
    @Order(1)
    void testAddBook() {
        Book book = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .language("English")
                .publicationYear(2023)
                .price(BigDecimal.valueOf(19.99))
                .quantity(10)
                .build();

        Book savedBook = bookService.saveBook(book);
        testBookId = savedBook.getId();

        Assertions.assertNotNull(savedBook.getId());
        Assertions.assertEquals("Test Book", savedBook.getTitle());
    }

    @Test
    @Order(2)
    void testGetBookById() {
        Book book = bookService.getBookById(testBookId)
        .orElseThrow(() -> new RuntimeException(""));

        Assertions.assertNotNull(book);
        Assertions.assertEquals("Test Author", book.getAuthor());
    }

    @Test
    @Order(3)
    void testUpdateBook() {
        Book book = bookService.getBookById(testBookId)
                .orElseThrow(() -> new RuntimeException(""));
        book.setPrice(BigDecimal.valueOf(24.99));

        Book updatedBook = bookService.saveBook(book);
        Assertions.assertEquals(0, BigDecimal.valueOf(24.99).compareTo(updatedBook.getPrice()));
    }

    @Test
    @Order(4)
    void testGetAllBooks() {
        List<Book> books = bookService.getAllBooks();
        Assertions.assertFalse(books.isEmpty());
        Assertions.assertTrue(books.stream().anyMatch(b -> b.getId().equals(testBookId)));
    }

    @Test
    @Order(5)
    void testAddToCart() {
        Book book = bookService.saveBook(Book.builder()
                .title("Тестовая книга").author("Автор").language("RU")
                .publicationYear(2023).price(BigDecimal.valueOf(200)).quantity(1).build());

        User user = userService.save(User.builder()
                .email("cart@test.com").password("pass").username("cartuser").role(User.Role.valueOf("CUSTOMER")).build());

        CartItem cartItem = cartService.addToCart(user.getId(), book.getId(), 1);

        Assertions.assertNotNull(cartItem);
        Assertions.assertEquals(1, cartItem.getQuantity());
    }


}