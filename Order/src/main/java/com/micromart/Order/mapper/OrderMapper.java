package com.micromart.Order.mapper;

import com.micromart.Order.entities.Order;
import com.micromart.Order.entities.OrderLineItems;
import com.micromart.Order.model.OrderResponse;
import com.micromart.Order.model.requests.OrderItemRequest;
import com.micromart.Order.model.responses.OrderItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public List<OrderLineItems> mapToEntityList(List<OrderItemRequest> itemRequests) {
        if (itemRequests == null) return List.of();

        return itemRequests.stream()
                .map(item -> OrderLineItems.builder()
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    public OrderResponse mapToResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = order.getOrderLineItemsList().stream()
                .map(item -> OrderItemResponse.builder()
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .userEmail(order.getUserEmail())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .cancellationReason(order.getCancellationReason())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
