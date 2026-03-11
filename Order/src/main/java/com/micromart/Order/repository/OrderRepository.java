package com.micromart.Order.repository;

import com.micromart.Order.entities.Order;
import com.micromart.Order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByUserEmailAndOrderStatus(String userEmail, OrderStatus orderStatus);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByUserEmail(String userEmail);
}