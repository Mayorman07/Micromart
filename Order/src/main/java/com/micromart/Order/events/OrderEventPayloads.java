package com.micromart.Order.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class OrderEventPayloads {

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class EventLineItem {
        private String skuCode;
        private String productName;
        private Integer quantity;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeductStockEvent {
        private String orderNumber;
        private List<EventLineItem> items;
    }

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class ClearCartEvent {
        private String userEmail;
    }

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class OrderReceiptEvent {
        private String orderNumber;
        private String userEmail;
        private BigDecimal totalAmount;
        private List<EventLineItem> items;
    }
}