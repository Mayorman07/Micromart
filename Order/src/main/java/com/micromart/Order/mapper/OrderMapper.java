package com.micromart.Order.mapper;

import com.micromart.Order.entities.Order;
import com.micromart.Order.entities.OrderLineItems;
import com.micromart.Order.model.responses.OrderResponse;
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
                .map(item -> new OrderLineItems(
                        item.getSkuCode(),
                        item.getProductName(),
                        item.getImageUrl(),
                        item.getUnitPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    public OrderResponse mapToResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = order.getOrderLineItemsList().stream()
                .map(item -> new OrderItemResponse(
                        item.getSkuCode(),
                        item.getProductName(),
                        item.getImageUrl(),
                        item.getUnitPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getOrderNumber(),
                order.getUserEmail(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getCancellationReason(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
