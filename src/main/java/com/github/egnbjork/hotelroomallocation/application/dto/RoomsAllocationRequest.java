package com.github.egnbjork.hotelroomallocation.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record RoomsAllocationRequest(
        @NotNull(message = "Premium room count must not be null")
        @Positive(message = "Premium room count must be positive")
        Integer premiumRooms,

        @NotNull(message = "Economy room count must not be null")
        @Positive(message = "Economy room count must be positive")
        Integer economyRooms,

        @NotNull(message = "Potential guests list must not be null")
        @NotEmpty(message = "Potential guests list must not be empty")
        List<
                @NotNull(message = "Guest offer must not be null")
                @DecimalMin(value = "0.0", inclusive = false, message = "Guest offer must be positive")
                        BigDecimal
                > potentialGuests) {
}
