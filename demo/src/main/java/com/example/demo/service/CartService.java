package com.example.demo.service;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.demo.model.*;
import com.example.demo.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final UserRepository userRepository;
    private final BookService bookService;
    private final CartItemRepository cartItemRepository;


    @Transactional
    public CartItem addToCart(Long userId, Long bookId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Пользователь не найден. Пожалуйста, войдите снова"
                ));

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга не найдена"));

        CartItem cartItem = cartItemRepository.findByUserAndBook(user, book)
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .book(book)
                        .quantity(0)
                        .build());

        cartItem.increaseQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public Integer getQuantityInCart(Long userId, Long bookId) {
        return cartItemRepository.findByUserAndBook(
                        User.builder().id(userId).build(),
                        Book.builder().id(bookId).build()
                )
                .map(CartItem::getQuantity)
                .orElse(0);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.clearCart(userId);
    }

    @Transactional
    public BigDecimal calculateTotal(Long userId) {
        return cartItemRepository.findByUser(User.builder().id(userId).build())
                .stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP); // Округление до 2 знаков после запятой
    }

    public CartItem findById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга не найдена с id: " + id));
    }

    public void save(CartItem item) {
         cartItemRepository.save(item);
    }

    public List<CartItem> getCartItems(Long id) {
        return cartItemRepository.findByUserId(id);
    }

    public void removeItem(Long id) {
         cartItemRepository.deleteItemById (id);
    }

    public boolean isItemBelongsToUser(Long itemId, Long userId) {
        return cartItemRepository.existsByIdAndUserId(itemId, userId);
    }
}