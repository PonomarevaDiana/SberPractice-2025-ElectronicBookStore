package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldCalculateTotal() {

        Long userId = 1L;
        Book book = new Book();
        book.setPrice(new BigDecimal("500"));

        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(2);

        when(bookRepository.findById(any())).thenReturn(Optional.of(book));

        Order order = orderService.createOrder(userId, List.of(item));

        assertEquals(new BigDecimal("1000"), order.getTotal());
    }
}