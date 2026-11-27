package com.backendbadi.orders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import com.backendbadi.orders.controller.OrderController;
import com.backendbadi.orders.entity.OrderStatus;
import com.backendbadi.orders.exception.OrderNotFoundException;
import com.backendbadi.orders.model.OrderRequest;
import com.backendbadi.orders.model.OrderResponse;
import com.backendbadi.orders.service.OrderService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Web layer only: no database, no RabbitMQ. The service is a Mockito mock. */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private static OrderResponse placed(long id) {
        return new OrderResponse(id, "Ravi", "Masala Dosa", 2, OrderStatus.PLACED, Instant.now());
    }

    @Test
    void placingAnOrderReturns201() throws Exception {
        given(orderService.placeOrder(any(OrderRequest.class))).willReturn(placed(1));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"Ravi","item":"Masala Dosa","quantity":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/1"))
                .andExpect(jsonPath("$.status").value("PLACED"));
    }

    @Test
    void invalidOrderReturns400() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"","item":"Idli","quantity":0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsOrders() throws Exception {
        given(orderService.findAll()).willReturn(List.of(placed(1), placed(2)));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void unknownOrderReturns404ProblemDetail() throws Exception {
        given(orderService.findById(999L)).willThrow(new OrderNotFoundException(999L));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order not found"))
                .andExpect(jsonPath("$.instance").value("/api/orders/999"));
    }
}
