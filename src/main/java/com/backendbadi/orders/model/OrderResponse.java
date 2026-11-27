package com.backendbadi.orders.model;

import java.time.Instant;

import com.backendbadi.orders.entity.OrderStatus;
import com.backendbadi.orders.entity.TiffinOrder;

/** What the API returns for an order. */
public record OrderResponse(Long id, String customerName, String item, int quantity, OrderStatus status,
        Instant placedAt) {

    public static OrderResponse from(TiffinOrder order) {
        return new OrderResponse(order.getId(), order.getCustomerName(), order.getItem(), order.getQuantity(),
                order.getStatus(), order.getPlacedAt());
    }
}
