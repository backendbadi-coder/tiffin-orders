package com.backendbadi.orders.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbitmq.client.RabbitAmqpTemplate;
import org.springframework.stereotype.Component;

/** The counter side: tells the kitchen that a new order was placed. */
@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitAmqpTemplate rabbitAmqpTemplate;

    public OrderEventPublisher(RabbitAmqpTemplate rabbitAmqpTemplate) {
        this.rabbitAmqpTemplate = rabbitAmqpTemplate;
    }

    public void publish(OrderPlacedEvent event) {
        // AMQP 1.0: asynchronous. The future completes with true when RabbitMQ ACCEPTS the message.
        rabbitAmqpTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event)
            .whenComplete((accepted, ex) -> {
                if (ex != null) {
                    log.error("Kitchen did NOT get order {}", event.orderId(), ex);
                }
                else {
                    log.info("Counter → kitchen (AMQP 1.0): order {} accepted by RabbitMQ", event.orderId());
                }
            });
    }
}
