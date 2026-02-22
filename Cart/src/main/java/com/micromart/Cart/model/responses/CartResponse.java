package com.micromart.Cart.model.responses;

import com.micromart.Cart.model.dto.CartItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private Long cartId;             // ✅ Consistent Long type
    private String userId;           // ✅ Match JWT token format
    private List<CartItemDto> items; // ✅ CRITICAL: List of items
    private Integer itemCount;       // Sum of all quantities
    private BigDecimal totalAmount;  // Sum of all subtotals
    private boolean isEmpty;// Trigger "Your cart is empty" UI
    private String message;
}
