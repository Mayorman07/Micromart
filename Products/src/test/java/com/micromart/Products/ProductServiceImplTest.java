package com.micromart.Products;

import com.micromart.products.client.InventoryClient;
import com.micromart.products.entity.Category;
import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.exceptions.NotFoundException;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.repository.ProductRepository;
import com.micromart.products.services.ProductServiceImpl;
import com.micromart.products.utils.CategoryIdValidator;
import com.micromart.products.utils.SkuCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ModelMapper modelMapper;
    @Mock private ProductRepository productRepository;
    @Mock private SkuCodeGenerator skuCodeGenerator;
    @Mock private InventoryClient inventoryClient;
    @Mock private CategoryIdValidator categoryIdValidator;
    @InjectMocks
    @Spy
    private ProductServiceImpl productService;

    private ProductDto productDto;
    private Product mockProduct;
    private Category mockCategory;

    private final String SKU_CODE = "TECH-LAPTOP-001";

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setBrand("Tech");
        productDto.setCategoryId(1L);
        productDto.setStockQuantity(10);
        productDto.setSkuCode(SKU_CODE);

        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Electronics");

        mockProduct = new Product();
        mockProduct.setId(99L);
        mockProduct.setName("Laptop");
        mockProduct.setSkuCode(SKU_CODE);
        mockProduct.setPrice(BigDecimal.valueOf(1000));
        mockProduct.setCategory(mockCategory);
    }

    // ==========================================
    // CREATE PRODUCT & COMPENSATION TESTS
    // ==========================================

    @Test
    @DisplayName("createProduct - Success: Saves product and initializes inventory stock")
    void createProduct_Success() {
        // Arrange
        doReturn(mockProduct).when(productService).saveProductToDatabase(productDto, SKU_CODE);

        doNothing().when(inventoryClient).initializeStock(SKU_CODE, 10);
        when(modelMapper.map(mockProduct, ProductDto.class)).thenReturn(productDto);

        // Act
        ProductDto result = productService.createProduct(productDto);

        // Assert
        assertNotNull(result);
        verify(productService, times(1)).saveProductToDatabase(productDto, SKU_CODE);
        verify(inventoryClient, times(1)).initializeStock(SKU_CODE, 10);

        // Ensure compensation was NOT called
        verify(productService, never()).compensateDeleteProduct(anyLong());
    }

    @Test
    @DisplayName("createProduct - Failure: Triggers compensation delete if Inventory API fails")
    void createProduct_InventoryFails_TriggersCompensation() {
        // Arrange
        doReturn(mockProduct).when(productService).saveProductToDatabase(productDto, SKU_CODE);

        doThrow(new RuntimeException("Inventory Service Offline"))
                .when(inventoryClient).initializeStock(anyString(), anyInt());

        // We stub compensateDeleteProduct to do nothing, we just want to verify it was CALLED
        doNothing().when(productService).compensateDeleteProduct(99L);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                productService.createProduct(productDto)
        );

        assertTrue(exception.getMessage().contains("Inventory Service Offline"));

        // VERIFY SAGA LOGIC: Did the service attempt to delete the ghost product?
        verify(productService, times(1)).compensateDeleteProduct(99L);
    }

    // ==========================================
    // SAVE PRODUCT LOGIC TESTS
    // ==========================================

    @Test
    @DisplayName("saveProductToDatabase - Success: Maps details and saves to repository")
    void saveProductToDatabase_Success() {
        // Arrange
        when(productRepository.existsBySkuCode(SKU_CODE)).thenReturn(false);
        when(categoryIdValidator.getCategoryOrThrow(1L)).thenReturn(mockCategory);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        Product result = productService.saveProductToDatabase(productDto, SKU_CODE);

        // Assert
        assertNotNull(result);
        assertEquals(SKU_CODE, result.getSkuCode());
        assertEquals("Electronics", result.getCategory().getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("saveProductToDatabase - Failure: Throws AlreadyExists if SKU is taken")
    void saveProductToDatabase_SkuExists_ThrowsException() {
        // Arrange
        when(productRepository.existsBySkuCode(SKU_CODE)).thenReturn(true);

        // Act & Assert
        assertThrows(AlreadyExistsException.class, () ->
                productService.saveProductToDatabase(productDto, SKU_CODE)
        );

        verify(productRepository, never()).save(any());
    }
}
