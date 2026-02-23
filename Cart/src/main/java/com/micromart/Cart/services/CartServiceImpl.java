package com.micromart.Cart.services;

import com.micromart.Cart.model.dto.CartDto;
import com.micromart.Cart.model.requests.CartRequest;

public class CartServiceImpl implements CartService{
    @Override
    public CartDto addItem(String userId, CartRequest cartRequest) {
        return null;
    }

    @Override
    public CartDto updateQuantity(String userId, CartRequest cartRequest) {
        return null;
    }

    @Override
    public CartDto removeItemFromCart(String userId, String skuCode) {
        return null;
    }

    @Override
    public CartDto viewCart(String userId) {
        return null;
    }

    @Override
    public CartDto clearUserCart(String userId) {
        return null;
    }
}
