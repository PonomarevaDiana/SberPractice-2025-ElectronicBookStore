package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class BookStatusTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testStockStatus() {
        Book book = new Book();
        book.setTitle("Книга");
        book.setQuantity(3);
        bookRepository.save(book);

        assertEquals("осталось мало", book.getAvailabilityStatus());

        book.setQuantity(0);
        assertEquals("товар закончился", book.getAvailabilityStatus());

        book.setQuantity(10);
        assertEquals("в наличии", book.getAvailabilityStatus());
    }
}