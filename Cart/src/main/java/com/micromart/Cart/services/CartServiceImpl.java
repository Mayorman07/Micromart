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
import com.micromart.Cart.model.responses.ProductResponse;
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


    private CartDto mapToCartDto(Cart cart) {
        CartDto cartDto = modelMapper.map(cart, CartDto.class);

        // These now safely hit your custom @Query methods
        Integer totalItems = cartRepository.countTotalItemsByUserId(cart.getUserId());
        BigDecimal totalAmount = cartRepository.calculateTotalAmountByUserId(cart.getUserId());

        // No more null checks needed thanks to your COALESCE in the SQL!
        cartDto.setItemCount(totalItems);
        cartDto.setTotalAmount(totalAmount);

        // If totalItems is 0, the cart is empty
        cartDto.setEmpty(totalItems == 0);
        cartDto.setMessage("Cart retrieved successfully");

        return cartDto;
    }
}
