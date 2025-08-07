package com.github.egnbjork.hotelroomallocation.domain.model;

import com.github.egnbjork.hotelroomallocation.domain.model.exception.RoomsAllocationMetricsException;

import java.math.BigDecimal;

public record RoomsAllocationMetrics(
        RoomType roomType,
        int usageCount,
        BigDecimal revenue
) {
    public RoomsAllocationMetrics {
        if (roomType == null) {
            throw new RoomsAllocationMetricsException("roomType must not be null");
        }
        if (usageCount < 0) {
            throw new RoomsAllocationMetricsException("usageCount must be non-negative");
        }
        if (revenue == null) {
            throw new RoomsAllocationMetricsException("revenue must not be null");
        }
        if (revenue.compareTo(BigDecimal.ZERO) < 0) {
            throw new RoomsAllocationMetricsException("revenue must be non-negative");
        }
    }
}
