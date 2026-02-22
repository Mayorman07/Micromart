package com.micromart.Cart.repository;

import com.micromart.Cart.entities.Cart;
import com.micromart.Cart.entities.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // === CART OPERATIONS ===

    /**
     * Find cart with items by user ID.
     * Uses EntityGraph to fetch the Cart and its Items in one join.
     */
    @EntityGraph(attributePaths = {"items"})
    Optional<Cart> findByUserId(String userId);

    // Check if cart exists for user
    boolean existsByUserId(String userId);

    // Delete entire cart by user ID
    @Transactional
    void deleteByUserId(String userId);

    // === CART ITEM OPERATIONS ===

    /**
     * Find specific item (like Beryl Glasses) in user's cart.
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.skuCode = :skuCode")
    Optional<CartItem> findByUserIdAndSkuCode(@Param("userId") String userId, @Param("skuCode") String skuCode);

    // Check if item exists in user's cart
    @Query("SELECT COUNT(ci) > 0 FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.skuCode = :skuCode")
    boolean existsByUserIdAndSkuCode(@Param("userId") String userId, @Param("skuCode") String skuCode);

    // Delete specific item from user's cart
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.skuCode = :skuCode")
    void deleteByUserIdAndSkuCode(@Param("userId") String userId, @Param("skuCode") String skuCode);

    // Delete all items from user's cart
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.userId = :userId")
    void deleteAllItemsByUserId(@Param("userId") String userId);

    // Count total items in user's cart for the Navbar badge
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.cart.userId = :userId")
    Integer countTotalItemsByUserId(@Param("userId") String userId);

    // Calculate total amount in user's cart for Checkout
    @Query("SELECT COALESCE(SUM(ci.quantity * ci.unitPrice), 0) FROM CartItem ci WHERE ci.cart.userId = :userId")
    BigDecimal calculateTotalAmountByUserId(@Param("userId") String userId);
}