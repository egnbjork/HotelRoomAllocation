package com.github.egnbjork.hotelroomallocation.domain;

import com.github.egnbjork.hotelroomallocation.domain.model.*;
import com.github.egnbjork.hotelroomallocation.domain.model.exception.AvailableRoomsException;
import com.github.egnbjork.hotelroomallocation.domain.ports.AllocateRooms;

import java.math.BigDecimal;
import java.util.List;

public class RoomAllocator implements AllocateRooms {
    public RoomsAllocationResult handle(List<AvailableRooms> availableRooms, List<Guest> guests) {
        var premiumRooms = availableRooms.stream()
                .filter(n -> n.roomType() == RoomType.PREMIUM)
                .findFirst()
                .orElseThrow(() -> new AvailableRoomsException("there are no premium rooms provided"));
        var premiumRoomsMetrics = assignPremiumGuests(premiumRooms, guests);

        return new RoomsAllocationResult(List.of(premiumRoomsMetrics));
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
