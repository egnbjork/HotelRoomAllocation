package com.github.egnbjork.hotelroomallocation.domain;

import com.github.egnbjork.hotelroomallocation.domain.model.*;
import com.github.egnbjork.hotelroomallocation.domain.model.exception.AvailableRoomsException;
import com.github.egnbjork.hotelroomallocation.domain.ports.AllocateRooms;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoomAllocator implements AllocateRooms {
    public RoomsAllocationResult handle(List<AvailableRooms> availableRooms, List<Guest> guests) {
        Map<RoomType, AvailableRooms> roomsByType = availableRooms.stream().collect(Collectors.toMap(
                AvailableRooms::roomType,
                Function.identity()
        ));

        if (!roomsByType.containsKey(RoomType.PREMIUM)) {
            throw new AvailableRoomsException("there are no premium rooms provided");
        }
        var premiumRoomsMetrics = assignPremiumGuests(roomsByType.get(RoomType.PREMIUM), guests);

        var economyGuests = guests.stream().filter(n -> !n.isPremium()).toList();
        var economyRevenue = economyGuests.stream()
                .map(Guest::offerPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var economyRoomsMetrics = new RoomsAllocationMetrics(RoomType.ECONOMY, economyGuests.size(), economyRevenue);

        return new RoomsAllocationResult(List.of(premiumRoomsMetrics, economyRoomsMetrics));
    }

    private RoomsAllocationMetrics assignPremiumGuests(AvailableRooms premiumRooms, List<Guest> guests) {
        var limitedPremiumGuests = guests.stream()
                .filter(Guest::isPremium)
                //TODO: what to do if there are more guests than rooms
                .limit(premiumRooms.roomCount())
                .toList();

        var revenue = limitedPremiumGuests.stream()
                .map(Guest::offerPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RoomsAllocationMetrics(RoomType.PREMIUM, limitedPremiumGuests.size(), revenue);
    }
}
