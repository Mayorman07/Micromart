package com.micromart.Cart.services;

import com.micromart.Cart.client.InventoryClient;
import com.micromart.Cart.client.ProductClient;
import com.micromart.Cart.entities.Cart;
import com.micromart.Cart.entities.CartItem;
import com.micromart.Cart.exceptions.CartBusinessException;
import com.micromart.Cart.model.dto.CartDto;
import com.micromart.Cart.model.meta.ProductMetadata;
import com.micromart.Cart.model.requests.CartRequest;
import com.micromart.Cart.model.responses.InventoryResponse;
import com.micromart.Cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    private CartService cartService;
    private final ModelMapper modelMapper;
    private final InventoryClient inventoryClient;
    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    @Override
    @Transactional
    public CartDto addItem(String userId, CartRequest cartRequest) {
        String skuCode = cartRequest.getSkuCode();
        Integer quantityToAdd = cartRequest.getQuantity();

        logger.info("Adding {} of SKU {} to cart for user {}", quantityToAdd, skuCode, userId);

        List<InventoryResponse> inventoryResponses = inventoryClient.isInStock(List.of(skuCode));

        if (inventoryResponses == null || inventoryResponses.isEmpty()) {
            throw new CartBusinessException("SKU not found in inventory: " + skuCode);
        }
        InventoryResponse inventory = inventoryResponses.get(0);
        if (inventory.getQuantity() < quantityToAdd) {
            throw new CartBusinessException("Insufficient stock. Only " + inventory.getQuantity() + " items available.");
        }
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("Creating new cart for user {}", userId);
                    return Cart.builder().userId(userId).build();
                });
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getSkuCode().equals(skuCode))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newTotalQuantity = existingItem.getQuantity() + quantityToAdd;
            if (inventory.getQuantity() < newTotalQuantity) {
                throw new CartBusinessException("Cannot add more. Inventory limit reached. Only " + inventory.getQuantity() + " total available.");
            }
            existingItem.setQuantity(newTotalQuantity);
        } else {
            List<ProductMetadata> metadataList = productClient.getProductMetadataBatch(List.of(skuCode));
            if (metadataList == null || metadataList.isEmpty()) {
                throw new CartBusinessException("Product details not found for SKU: " + skuCode);
            }

            ProductMetadata productMeta = metadataList.get(0);
            CartItem newItem = CartItem.builder()
                    .skuCode(skuCode)
                    .quantity(quantityToAdd)
                    .productName(productMeta.getName())
                    .unitPrice(productMeta.getPrice())
                    .imageUrl(productMeta.getImageUrl())
                    .build();
            cart.addItem(newItem);
        }
        Cart savedCart = cartRepository.save(cart);

        return mapToCartDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto updateQuantity(String userId, CartRequest cartRequest) {
        String skuCode = cartRequest.getSkuCode();
        Integer newQuantity = cartRequest.getQuantity();

        logger.info("Updating SKU: {} to quantity: {} for user: {}", skuCode, newQuantity, userId);

        if (newQuantity == null || newQuantity <= 0) {
            logger.info("Quantity reduced to 0. Auto-removing SKU: {} for user: {}", skuCode, userId);
            return removeItemFromCart(userId, skuCode);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartBusinessException("Cart not found for user: " + userId));

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getSkuCode().equals(skuCode))
                .findFirst()
                .orElseThrow(() -> new CartBusinessException("Item not found in cart: " + skuCode));

        if (newQuantity > itemToUpdate.getQuantity()) {
            List<InventoryResponse> inventoryResponses = inventoryClient.isInStock(List.of(skuCode));

            if (inventoryResponses == null || inventoryResponses.isEmpty()) {
                throw new CartBusinessException("SKU not found in inventory: " + skuCode);
            }

            InventoryResponse inventory = inventoryResponses.get(0);

            if (inventory.getQuantity() < newQuantity) {
                throw new CartBusinessException("Insufficient stock. Only " + inventory.getQuantity() + " items available.");
            }
        }
        itemToUpdate.setQuantity(newQuantity);
        Cart savedCart = cartRepository.save(cart);
        return mapToCartDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto removeItemFromCart(String userId, String skuCode) {
        logger.info("Removing SKU: {} from cart for user: {}", skuCode, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartBusinessException("Cart not found for user: " + userId));

        boolean removed = cart.getItems().removeIf(item -> item.getSkuCode().equals(skuCode));

        if (!removed) {
            logger.warn("Attempted to remove SKU {}, but it was not found in the cart for user {}", skuCode, userId);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartDto(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto viewCart(String userId) {
        logger.info("Fetching cart for user: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder().userId(userId).build());
        return mapToCartDto(cart);

    }

    @Override
    @Transactional
    public CartDto clearUserCart(String userId) {
        logger.info("Clearing entire cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartBusinessException("Cart not found for user: " + userId));

        cart.getItems().clear();
        Cart savedCart = cartRepository.save(cart);
        return mapToCartDto(savedCart);
    }


    private CartDto mapToCartDto(Cart cart) {
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        Integer totalItems = cartRepository.countTotalItemsByUserId(cart.getUserId());
        BigDecimal totalAmount = cartRepository.calculateTotalAmountByUserId(cart.getUserId());
        cartDto.setItemCount(totalItems);
        cartDto.setTotalAmount(totalAmount);
        cartDto.setEmpty(totalItems == 0);
        cartDto.setMessage("Cart retrieved successfully");

        return cartDto;
    }
}
