package com.github.egnbjork.hotelroomallocation.domain.model;

import java.util.List;

public record RoomsAllocationResult(List<RoomsAllocationMetrics> allocationMetrics) {
}
