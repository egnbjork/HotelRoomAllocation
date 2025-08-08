package com.github.egnbjork.hotelroomallocation.application.mappers;

import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import com.github.egnbjork.hotelroomallocation.domain.model.*;
import com.github.egnbjork.hotelroomallocation.infrastructure.EnvironmentVariables;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RoomsAllocationDataMapper {

    private final EnvironmentVariables environmentVariables;

    public RoomsAllocationDataMapper(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<AvailableRooms> toAvailableRooms(RoomsAllocationRequest roomsAllocationRequest) {
        AvailableRooms premiumRooms = new AvailableRooms(RoomType.PREMIUM, roomsAllocationRequest.premiumRooms());
        AvailableRooms economyRooms = new AvailableRooms(RoomType.ECONOMY, roomsAllocationRequest.economyRooms());
        return List.of(premiumRooms, economyRooms);
    }

    public List<Guest> toGuests(RoomsAllocationRequest roomsAllocationRequest) {
        return roomsAllocationRequest
                .potentialGuests()
                .stream()
                .map(n -> new Guest(environmentVariables.premiumPriceThreshold, n))
                .toList();
    }

    public RoomsAllocationResponse toRoomAllocationResponse(RoomsAllocationResult result) {
        int usagePremium = 0;
        BigDecimal revenuePremium = BigDecimal.ZERO;
        int usageEconomy = 0;
        BigDecimal revenueEconomy = BigDecimal.ZERO;

        for (RoomsAllocationMetrics metrics : result.allocationMetrics()) {
            if (metrics.roomType() == RoomType.PREMIUM) {
                usagePremium = metrics.usageCount();
                revenuePremium = metrics.revenue();
            } else if (metrics.roomType() == RoomType.ECONOMY) {
                usageEconomy = metrics.usageCount();
                revenueEconomy = metrics.revenue();
            }
        }

        return new RoomsAllocationResponse(
                usagePremium,
                revenuePremium,
                usageEconomy,
                revenueEconomy
        );
    }
}
