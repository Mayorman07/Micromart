package com.micromart.Cart.services;

import com.micromart.Cart.model.dto.CartDto;
import com.micromart.Cart.model.dto.CartItemDto;
import com.micromart.Cart.model.requests.CartRequest;
import com.micromart.Cart.model.responses.CartResponse;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    CartDto addItem(String userId, CartRequest cartRequest);

    CartDto updateQuantity(String userId,CartRequest cartRequest);

    CartDto removeItemFromCart(String userId, String skuCode);

    CartDto viewCart(String userId);
    CartDto clearUserCart(String userId);

}
