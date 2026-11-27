package com.backendbadi.orders.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ topology: counter → exchange "tiffin.orders" → (routing key "order.placed") → queue "kitchen.orders".
 * Spring Boot's auto-configured admin declares these beans on the broker at startup.
 */
@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    public static final String EXCHANGE = "tiffin.orders";
    public static final String ROUTING_KEY = "order.placed";
    public static final String KITCHEN_QUEUE = "kitchen.orders";

    @Bean
    TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue kitchenQueue() {
        return QueueBuilder.durable(KITCHEN_QUEUE).build();
    }

    @Bean
    Binding kitchenBinding(Queue kitchenQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(kitchenQueue).to(ordersExchange).with(ROUTING_KEY);
    }

    /** Send and receive messages as JSON (Jackson 3). Spring Boot uses this bean for the template and listeners. */
    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter("com.backendbadi.orders.messaging");
    }
}
