package com.micromart.Cart.services;

import com.micromart.Cart.model.dto.CartItemDto;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    CartItemDto removeItemFromCart(Long cartId, String skuCode);

    CartItemDto clearUserCart(Long cartId);

    CartItemDto updateQuantity(Long cartId, String skuCode, Integer newQuantity);

    CartItemDto addItemToCart(Long cartId, String skuCode);

    CartItemDto viewCart(Long cartId);

}
