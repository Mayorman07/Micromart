package com.micromart.Order.events;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderEventPayloads {

    public static class EventLineItem {
        private String skuCode;
        private String productName;
        private Integer quantity;

        public EventLineItem(String skuCode, String productName, Integer quantity) {
            this.skuCode = skuCode;
            this.productName = productName;
            this.quantity = quantity;
        }

        public String getSkuCode() { return skuCode; }
        public String getProductName() { return productName; }
        public Integer getQuantity() { return quantity; }
    }

    public static class DeductStockEvent {
        private String orderNumber;
        private List<EventLineItem> items;

        public DeductStockEvent(String orderNumber, List<EventLineItem> items) {
            this.orderNumber = orderNumber;
            this.items = items;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public List<EventLineItem> getItems() {
            return items;
        }

        public void setItems(List<EventLineItem> items) {
            this.items = items;
        }
    }

    public static class ClearCartEvent {
        private String userEmail;

        public ClearCartEvent(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }
    }

    public static class OrderReceiptEvent {
        private String orderNumber;
        private String userEmail;
        private BigDecimal totalAmount;
        private List<EventLineItem> items;

        public OrderReceiptEvent(String orderNumber, String userEmail, BigDecimal totalAmount, List<EventLineItem> items) {
            this.orderNumber = orderNumber;
            this.userEmail = userEmail;
            this.totalAmount = totalAmount;
            this.items = items;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
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

        public List<EventLineItem> getItems() {
            return items;
        }

        public void setItems(List<EventLineItem> items) {
            this.items = items;
        }
    }
}