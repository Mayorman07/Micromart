package com.micromart.Cart.services;

import com.micromart.Cart.model.dto.CartDto;
import com.micromart.Cart.model.dto.CartItemDto;
import com.micromart.Cart.model.requests.CartRequest;
import com.micromart.Cart.model.responses.CartResponse;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    // Add item to cart (creates cart if doesn't exist)
    CartDto addItem(String userId, CartRequest cartRequest);

    // Update quantity of existing item
    CartDto updateQuantity(String userId,CartRequest cartRequest);

    CartDto removeItemFromCart(String userId, String skuCode);

    CartDto viewCart(String userId);
    CartDto clearUserCart(String userId);

}
