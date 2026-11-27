package com.backendbadi.orders.messaging;

/**
 * The message the counter sends to the kitchen. Sent as JSON.
 * Keep it small: the kitchen only needs to know what to cook.
 */
public record OrderPlacedEvent(Long orderId, String customerName, String item, int quantity) {
}
