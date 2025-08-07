package com.github.egnbjork.hotelroomallocation.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomsAllocationResultTest {

    private RoomsAllocationResult roomsAllocationResult;

    @BeforeEach
    void setUp() {
        roomsAllocationResult = new RoomsAllocationResult(List.of(
                new RoomsAllocationMetrics(RoomType.PREMIUM, 2, new BigDecimal("400")),
                new RoomsAllocationMetrics(RoomType.ECONOMY, 3, new BigDecimal("180"))
        ));
    }

    @Test
    @DisplayName("should return metrics for existing room type")
    void shouldReturnMetricsForExistingRoomType() {
        Optional<RoomsAllocationMetrics> metrics = roomsAllocationResult.getRoomMetricsForType(RoomType.PREMIUM);

        assertTrue(metrics.isPresent());
        assertEquals(RoomType.PREMIUM, metrics.get().roomType());
        assertEquals(2, metrics.get().usageCount());
        assertEquals(new BigDecimal("400"), metrics.get().revenue());
    }
}
