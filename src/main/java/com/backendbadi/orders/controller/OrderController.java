package com.backendbadi.orders.controller;

import java.net.URI;
import java.util.List;

import com.backendbadi.orders.model.OrderRequest;
import com.backendbadi.orders.model.OrderResponse;
import com.backendbadi.orders.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** The counter: place an order. Returns 201 with status PLACED; the kitchen changes it to COOKING. */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse created = orderService.placeOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + created.id())).body(created);
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
