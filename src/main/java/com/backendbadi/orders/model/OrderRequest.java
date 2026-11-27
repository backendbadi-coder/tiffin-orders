package com.backendbadi.orders.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** JSON body of POST /api/orders. */
public record OrderRequest(
        @NotBlank @Size(max = 50) String customerName,
        @NotBlank @Size(max = 50) String item,
        @Min(1) @Max(20) int quantity) {
}
