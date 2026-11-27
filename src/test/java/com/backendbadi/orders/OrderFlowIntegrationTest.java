package com.backendbadi.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;

import com.backendbadi.orders.entity.OrderStatus;
import com.backendbadi.orders.model.OrderRequest;
import com.backendbadi.orders.model.OrderResponse;
import com.backendbadi.orders.service.OrderService;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

/**
 * End to end with a REAL RabbitMQ in a Docker container:
 * counter → exchange → queue → kitchen listener → status COOKING.
 * Skipped automatically when Docker isn't running.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class OrderFlowIntegrationTest {

    /** @ServiceConnection: Spring Boot points the app at this container (host, port, user, password). */
    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:4.2-management");

    @Autowired
    private OrderService orderService;

    @Test
    void kitchenPicksUpThePlacedOrder() {
        OrderResponse placed = orderService.placeOrder(new OrderRequest("Ravi", "Masala Dosa", 2));
        assertThat(placed.status()).isEqualTo(OrderStatus.PLACED);

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(orderService.findById(placed.id()).status())
                        .isEqualTo(OrderStatus.COOKING));
    }
}
