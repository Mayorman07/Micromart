package com.micromart.Cart.controller;

import com.micromart.Cart.model.dto.CartDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.micromart.Cart.model.requests.CartRequest;
import com.micromart.Cart.model.responses.CartResponse;
import com.micromart.Cart.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

   private final CartService cartService;
   private final ModelMapper modelMapper;
   private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    public CartController(CartService cartService, ModelMapper modelMapper){
        this.cartService=cartService;
        this.modelMapper=modelMapper;
    }

    @PostMapping("/items")
   public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal String userId,
                                               @RequestBody CartRequest cartRequest){
        logger.info("The incoming create cart request {} " , cartRequest);
        CartDto itemsInCart = cartService.addItem(userId, cartRequest);
        CartResponse returnValue = modelMapper.map(itemsInCart, CartResponse.class);
        logger.info("The outgoing create cart request {} " , returnValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
   }
    @PutMapping("/update")
   public ResponseEntity<CartResponse> updateQuantity(@AuthenticationPrincipal String userId,
                                                      @RequestBody CartRequest cartRequest){
       logger.info("The incoming update quantity request {} " , cartRequest);
        CartDto cartToBeUpdated = cartService.updateQuantity(userId,cartRequest);
        CartResponse returnValue = modelMapper.map(cartToBeUpdated, CartResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
   }
   @DeleteMapping("/remove/{skuCode}")
   public ResponseEntity<CartResponse> removeItemFromCart(@AuthenticationPrincipal String userId, @PathVariable String skuCode){
        CartDto cartWithItemsToBeUpdated = cartService.removeItemFromCart(userId,skuCode);
        CartResponse returnValue = modelMapper.map(cartWithItemsToBeUpdated, CartResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
   }
   @GetMapping
   public ResponseEntity<CartResponse> viewCart(@AuthenticationPrincipal String userId){
        CartDto cartToBeViewed = cartService.viewCart(userId);
        CartResponse returnValue = modelMapper.map(cartToBeViewed, CartResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
   }
   @DeleteMapping("/clear")
   public ResponseEntity<CartResponse> clearUserCart(@AuthenticationPrincipal String userId){
       CartDto cartToBeEmptied= cartService.clearUserCart(userId);
       CartResponse returnValue = modelMapper.map(cartToBeEmptied, CartResponse.class);
       return ResponseEntity.status(HttpStatus.OK).body(returnValue);
   }

}