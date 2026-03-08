package com.micromart.Payment.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    @DecimalMin(value = "0.01", message = "Total amount must be at least 0.01")
    private BigDecimal totalAmount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter ISO code (e.g., USD)")
    private String currency;

    @NotNull(message = "Payment method is required")
    @Pattern(regexp = "^(STRIPE|BANK_TRANSFER|CRYPTO)$", message = "Invalid payment method")
    private String paymentMethod;

    @NotNull(message = "At least one item is required")
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<OrderItemRequest> items;

    public PaymentRequest() {}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}