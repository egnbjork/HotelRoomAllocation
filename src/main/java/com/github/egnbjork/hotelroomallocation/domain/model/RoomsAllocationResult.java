package com.github.egnbjork.hotelroomallocation.domain.model;

import java.util.List;
import java.util.Optional;

public record RoomsAllocationResult(List<RoomsAllocationMetrics> allocationMetrics) {
    Optional<RoomsAllocationMetrics> getRoomMetricsForType(RoomType roomType) {
        return allocationMetrics.stream()
                .filter(m -> m.roomType() == roomType)
                .findFirst();
    }
}
