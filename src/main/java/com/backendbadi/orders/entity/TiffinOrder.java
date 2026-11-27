package com.backendbadi.orders.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One customer order, e.g. "Ravi: 2 x Masala Dosa". */
@Entity
@Table(name = "tiffin_orders")
public class TiffinOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String customerName;

    @Column(nullable = false, length = 50)
    private String item;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PLACED;

    @Column(nullable = false)
    private Instant placedAt = Instant.now();

    /** Required by JPA. */
    protected TiffinOrder() {
    }

    public TiffinOrder(String customerName, String item, int quantity) {
        this.customerName = customerName;
        this.item = item;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }
}
