package com.micromart.Order.repository;

import com.micromart.Order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserEmail(String userEmail);
    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    boolean existsByOrderNumber(String orderNumber);

    List<Order> findByStatus(String status);

    List<Order> findByUserEmailAndStatus(String userEmail, String status);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByUserEmail(String userEmail);
}