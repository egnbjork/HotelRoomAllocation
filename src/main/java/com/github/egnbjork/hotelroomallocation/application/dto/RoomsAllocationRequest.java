package com.github.egnbjork.hotelroomallocation.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record RoomsAllocationRequest(
        Integer premiumRooms,
        Integer economyRooms,
        List<BigDecimal> potentialGuests
) {
}
