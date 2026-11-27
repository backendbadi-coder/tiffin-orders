package com.backendbadi.orders.messaging;

import com.backendbadi.orders.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** The kitchen side: picks new orders from the queue and starts cooking. */
@Component
public class KitchenListener {

    private static final Logger log = LoggerFactory.getLogger(KitchenListener.class);

    private final OrderService orderService;

    public KitchenListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitConfig.KITCHEN_QUEUE)
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("Kitchen received order {}: {} x {} for {}", event.orderId(), event.quantity(), event.item(),
                event.customerName());
        orderService.startCooking(event.orderId());
    }
}
