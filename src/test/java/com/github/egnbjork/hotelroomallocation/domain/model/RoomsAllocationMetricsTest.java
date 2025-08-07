package com.github.egnbjork.hotelroomallocation.domain.model;

import com.github.egnbjork.hotelroomallocation.domain.model.exception.RoomsAllocationMetricsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RoomsAllocationMetricsTest {

    @Test
    @DisplayName("should create valid metrics")
    void shouldCreateValidMetrics() {
        var metrics = new RoomsAllocationMetrics(RoomType.PREMIUM, 5, BigDecimal.valueOf(100.50));

        assertThat(metrics.roomType()).isEqualTo(RoomType.PREMIUM);
        assertThat(metrics.usageCount()).isEqualTo(5);
        assertThat(metrics.revenue()).isEqualByComparingTo("100.50");
    }

    @Test
    @DisplayName("should fail if room type is null")
    void shouldThrowIfRoomTypeIsNull() {
        assertThatThrownBy(() -> new RoomsAllocationMetrics(null, 1, BigDecimal.TEN))
                .isInstanceOf(RoomsAllocationMetricsException.class)
                .hasMessage("roomType must not be null");
    }

    @Test
    @DisplayName("should fail if usage count is negative")
    void shouldThrowIfUsageCountIsNegative() {
        assertThatThrownBy(() -> new RoomsAllocationMetrics(RoomType.ECONOMY, -1, BigDecimal.TEN))
                .isInstanceOf(RoomsAllocationMetricsException.class)
                .hasMessage("usageCount must be non-negative");
    }

    @Test
    @DisplayName("should fail if revenue is null")
    void shouldThrowIfRevenueIsNull() {
        assertThatThrownBy(() -> new RoomsAllocationMetrics(RoomType.ECONOMY, 1, null))
                .isInstanceOf(RoomsAllocationMetricsException.class)
                .hasMessage("revenue must not be null");
    }

    @Test
    @DisplayName("should fail if revenue is negative")
    void shouldThrowIfRevenueIsNegative() {
        assertThatThrownBy(() -> new RoomsAllocationMetrics(RoomType.ECONOMY, 1, BigDecimal.valueOf(-0.01)))
                .isInstanceOf(RoomsAllocationMetricsException.class)
                .hasMessage("revenue must be non-negative");
    }
}
