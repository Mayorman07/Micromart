package com.micromart.Cart.service;

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
import com.micromart.Cart.services.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock private ModelMapper modelMapper;
    @Mock private InventoryClient inventoryClient;
    @Mock private CartRepository cartRepository;
    @Mock private ProductClient productClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartRequest cartRequest;
    private Cart cart;
    private CartItem cartItem;
    private InventoryResponse inventoryResponse;
    private ProductMetadata productMetadata;
    private CartDto cartDto;

    private final String USER_ID = "user-123";
    private final String SKU_CODE = "SKU-999";

    @BeforeEach
    void setUp() {
        // Setup Request
        cartRequest = new CartRequest();
        cartRequest.setSkuCode(SKU_CODE);
        cartRequest.setQuantity(2);

        // Setup Cart and Item
        cartItem = CartItem.builder()
                .skuCode(SKU_CODE)
                .quantity(1) // Already has 1 in cart
                .productName("Test Product")
                .unitPrice(BigDecimal.valueOf(10.0))
                .build();

        cart = Cart.builder()
                .userId(USER_ID)
                .build();

        cart.setItems(new ArrayList<>(List.of(cartItem)));

        // Setup Feign Client Responses
        inventoryResponse = new InventoryResponse();
        inventoryResponse.setSkuCode(SKU_CODE);
        inventoryResponse.setQuantity(50); // 50 in stock

        productMetadata = new ProductMetadata();
        productMetadata.setName("Test Product");
        productMetadata.setPrice(BigDecimal.valueOf(10.0));
        productMetadata.setImageUrl("http://image.url");

        // Setup DTO
        cartDto = new CartDto();
        cartDto.setUserId(USER_ID);
    }

    // Helper method to mock the internal mapToCartDto DB aggregations
    private void mockCartDtoMapping() {
        when(modelMapper.map(any(Cart.class), eq(CartDto.class))).thenReturn(cartDto);
        when(cartRepository.countTotalItemsByUserId(anyString())).thenReturn(3);
        when(cartRepository.calculateTotalAmountByUserId(anyString())).thenReturn(BigDecimal.valueOf(30.0));
    }

    // ==========================================
    // ADD ITEM TESTS
    // ==========================================

    @Test
    @DisplayName("addItem - Success: Adds completely new item to a new cart")
    void addItem_NewItem_Success() {
        // Arrange
        cart.setItems(new ArrayList<>()); // Empty cart

        when(inventoryClient.isInStock(List.of(SKU_CODE))).thenReturn(List.of(inventoryResponse));
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(productClient.getProductMetadataBatch(List.of(SKU_CODE))).thenReturn(List.of(productMetadata));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        mockCartDtoMapping();

        // Act
        CartDto result = cartService.addItem(USER_ID, cartRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, cart.getItems().size()); // Item was added
        assertEquals(2, cart.getItems().get(0).getQuantity()); // Quantity matches request
        verify(productClient, times(1)).getProductMetadataBatch(List.of(SKU_CODE));
    }

    @Test
    @DisplayName("addItem - Success: Updates quantity of existing item in cart")
    void addItem_ExistingItem_Success() {
        // Arrange (Cart already has 1 of SKU-999)
        when(inventoryClient.isInStock(List.of(SKU_CODE))).thenReturn(List.of(inventoryResponse));
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        mockCartDtoMapping();

        // Act
        CartDto result = cartService.addItem(USER_ID, cartRequest); // Adding 2 more

        // Assert
        assertNotNull(result);
        assertEquals(1, cart.getItems().size()); // No new item row created
        assertEquals(3, cart.getItems().get(0).getQuantity()); // 1 existing + 2 new = 3
        verify(productClient, never()).getProductMetadataBatch(anyList()); // Shouldn't fetch metadata again
    }

    @Test
    @DisplayName("addItem - Failure: Throws exception when inventory is insufficient")
    void addItem_InsufficientStock_ThrowsException() {
        // Arrange
        inventoryResponse.setQuantity(1); // Only 1 in stock, but request asks for 2
        when(inventoryClient.isInStock(List.of(SKU_CODE))).thenReturn(List.of(inventoryResponse));

        // Act & Assert
        CartBusinessException exception = assertThrows(CartBusinessException.class, () ->
                cartService.addItem(USER_ID, cartRequest)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(cartRepository, never()).save(any());
    }

    // ==========================================
    // UPDATE QUANTITY TESTS
    // ==========================================

    @Test
    @DisplayName("updateQuantity - Success: Updates item quantity when stock allows")
    void updateQuantity_Success() {
        // Arrange
        cartRequest.setQuantity(5); // Update to 5
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(inventoryClient.isInStock(List.of(SKU_CODE))).thenReturn(List.of(inventoryResponse)); // 50 in stock
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        mockCartDtoMapping();

        // Act
        CartDto result = cartService.updateQuantity(USER_ID, cartRequest);

        // Assert
        assertNotNull(result);
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    @DisplayName("updateQuantity - Success: Auto-removes item if quantity updated to 0")
    void updateQuantity_ZeroQuantity_RemovesItem() {
        // Arrange
        cartRequest.setQuantity(0); // This should trigger auto-remove
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        mockCartDtoMapping();

        // Act
        CartDto result = cartService.updateQuantity(USER_ID, cartRequest);

        // Assert
        assertNotNull(result);
        assertTrue(cart.getItems().isEmpty()); // Item should be gone
    }

    // ==========================================
    // REMOVE ITEM / CLEAR CART TESTS
    // ==========================================

    @Test
    @DisplayName("removeItemFromCart - Success: Removes specific item")
    void removeItemFromCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        mockCartDtoMapping();

        // Act
        CartDto result = cartService.removeItemFromCart(USER_ID, SKU_CODE);

        // Assert
        assertNotNull(result);
        assertTrue(cart.getItems().isEmpty()); // Cart should now be empty
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    @DisplayName("clearUserCart - Success: Clears all items from the cart")
    void clearUserCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        mockCartDtoMapping();

        // Act
        CartDto result = cartService.clearUserCart(USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(cart.getItems().isEmpty());
    }
}