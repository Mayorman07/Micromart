package com.micromart.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderReceiptEvent {
    private String orderNumber;
    private String userEmail;
    private BigDecimal totalAmount;
    private List<EventLineItem> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventLineItem {
        private String skuCode;
        private String productName;
        private Integer quantity;
    }
}
