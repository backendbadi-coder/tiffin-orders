package com.backendbadi.orders.service;

import java.util.List;

import com.backendbadi.orders.entity.OrderStatus;
import com.backendbadi.orders.entity.TiffinOrder;
import com.backendbadi.orders.exception.OrderNotFoundException;
import com.backendbadi.orders.messaging.OrderEventPublisher;
import com.backendbadi.orders.messaging.OrderPlacedEvent;
import com.backendbadi.orders.model.OrderRequest;
import com.backendbadi.orders.model.OrderResponse;
import com.backendbadi.orders.repository.TiffinOrderRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final TiffinOrderRepository repository;

    private final OrderEventPublisher publisher;

    public OrderService(TiffinOrderRepository repository, OrderEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    /**
     * Save the order, THEN tell the kitchen.
     * Deliberately not @Transactional: save() commits first, so the kitchen never receives
     * an order id that is not in the database yet.
     */
    public OrderResponse placeOrder(OrderRequest request) {
        TiffinOrder saved = repository.save(
                new TiffinOrder(request.customerName().trim(), request.item().trim(), request.quantity()));
        publisher.publish(new OrderPlacedEvent(saved.getId(), saved.getCustomerName(), saved.getItem(),
                saved.getQuantity()));
        return OrderResponse.from(saved);
    }

    /** Called by the kitchen when it picks the order from the queue. */
    @Transactional
    public void startCooking(Long orderId) {
        TiffinOrder order = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.COOKING);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return repository.findById(id).map(OrderResponse::from).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
