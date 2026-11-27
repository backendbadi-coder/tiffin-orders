package com.backendbadi.orders.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** The counter side: tells the kitchen that a new order was placed. */
@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(OrderPlacedEvent event) {
        // AMQP 0.9.1: a blocking call. It returns once the message is handed to the client library.
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);
        log.info("Counter → kitchen: order {} ({} x {})", event.orderId(), event.quantity(), event.item());
    }
}
