package com.backendbadi.orders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.backendbadi.orders.messaging.KitchenListener;
import com.backendbadi.orders.messaging.OrderPlacedEvent;
import com.backendbadi.orders.service.OrderService;

import org.junit.jupiter.api.Test;

/** The listener is a normal Java method: test it without RabbitMQ. */
class KitchenListenerTest {

    @Test
    void receivedOrderStartsCooking() {
        OrderService orderService = mock(OrderService.class);
        KitchenListener listener = new KitchenListener(orderService);

        listener.onOrderPlaced(new OrderPlacedEvent(5L, "Ravi", "Pesarattu", 1));

        verify(orderService).startCooking(5L);
    }
}
