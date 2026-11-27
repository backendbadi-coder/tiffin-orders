package com.backendbadi.orders.repository;

import com.backendbadi.orders.entity.TiffinOrder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TiffinOrderRepository extends JpaRepository<TiffinOrder, Long> {
}
