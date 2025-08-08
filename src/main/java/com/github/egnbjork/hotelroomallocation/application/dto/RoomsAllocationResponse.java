package com.github.egnbjork.hotelroomallocation.application.dto;

import java.math.BigDecimal;

public record RoomsAllocationResponse(
        int usagePremium,
        BigDecimal revenuePremium,
        int usageEconomy,
        BigDecimal revenueEconomy
) {
}
