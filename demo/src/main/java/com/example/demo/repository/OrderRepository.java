package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId " + "ORDER BY CASE WHEN o.status " +
            "= 'Получен' THEN 1 ELSE 0 END ASC, " + "o.orderDate DESC")
        List<Order> findByUserIdOrderByStatusAscOrderDateDesc(@Param("userId") long userId);

}