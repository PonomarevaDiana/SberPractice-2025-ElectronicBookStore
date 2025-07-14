package com.example.demo.repository;
import com.example.demo.model.Book;
import com.example.demo.model.CartItem;
import java.util.List;
import java.util.Optional;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndBook(User user, Book book);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void clearCart(Long userId);

   void deleteItemById(Long id);

    boolean existsByIdAndUserId(Long itemId, Long userId);
}
