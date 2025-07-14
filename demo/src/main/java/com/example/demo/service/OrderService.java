package com.example.demo.service;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;

    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("CREATED");
        order.setOrderDate(LocalDateTime.now());

        items.forEach(item -> {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("Книга не найдена"));

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(book.getPrice());
            orderItem.calculateTotalPrice();
            order.addItem(orderItem);
        });

        calculateOrderTotal(order);
        return orderRepository.save(order);
    }

    @Transactional
    public BigDecimal calculateOrderTotal(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return total;
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        Order order = new Order();
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("В обработке");
        order.setItems(new ArrayList<>());
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getBook() == null || cartItem.getBook().getPrice() == null) {
                throw new IllegalStateException("Цена книги не указана для: " +
                        (cartItem.getBook() != null ? cartItem.getBook().getTitle() : "неизвестная книга"));
            }

            if (cartItem.getQuantity() <= 0) {
                throw new IllegalStateException("Некорректное количество для: " + cartItem.getBook().getTitle());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getBook().getPrice());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);

            BigDecimal itemPrice = cartItem.getBook().getPrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(itemTotal);
        }
        order.setTotal(total);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrdersSorted(long userId) {
        return orderRepository.findByUserIdOrderByStatusAscOrderDateDesc(userId);
    }

    public Order getOrderWithItems(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найден"));
    }
}