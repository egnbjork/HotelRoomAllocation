package com.github.egnbjork.hotelroomallocation.domain;

import com.github.egnbjork.hotelroomallocation.domain.model.*;
import com.github.egnbjork.hotelroomallocation.domain.model.exception.AvailableRoomsException;
import com.github.egnbjork.hotelroomallocation.domain.ports.AllocateRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoomAllocator implements AllocateRooms {

    private static final Logger logger = LoggerFactory.getLogger(RoomAllocator.class);

    public RoomsAllocationResult handle(List<AvailableRooms> availableRooms, List<Guest> guests) {
        Map<RoomType, AvailableRooms> roomsByType = availableRooms.stream().collect(Collectors.toMap(
                AvailableRooms::roomType,
                Function.identity()
        ));

        var premiumPreBookedRooms = roomsByType.get(RoomType.PREMIUM);
        var economyPrebookedRooms = roomsByType.get(RoomType.ECONOMY);

        logger.info("[ROOMALLOCATOR] received available {} economy rooms, {} premium rooms",
                economyPrebookedRooms != null ? economyPrebookedRooms.roomCount() : "no economy rooms available",
                premiumPreBookedRooms != null ? premiumPreBookedRooms.roomCount() : "no premium rooms available");

        if (premiumPreBookedRooms == null) {
            throw new AvailableRoomsException("there are no premium rooms provided");
        }

        if (economyPrebookedRooms == null) {
            economyPrebookedRooms = new AvailableRooms(RoomType.ECONOMY, 0);
        }

        var premiumGuests = guests.stream()
                .filter(Guest::isPremium)
                .sorted(Comparator.comparing(Guest::offerPrice).reversed())
                .toList();
        logger.info("[ROOMALLOCATOR]: received {} premium guests", premiumGuests.size());

        //TODO: what to do if there are more premiumGuests than rooms
        if (premiumGuests.size() > premiumPreBookedRooms.roomCount()) {
            premiumGuests = premiumGuests.subList(0, premiumPreBookedRooms.roomCount());
        }

        var economyGuests = guests.stream()
                .filter(n -> !n.isPremium())
                .sorted(Comparator.comparing(Guest::offerPrice).reversed())
                .toList();
        logger.info("[ROOMALLOCATOR] received {} economy guests", economyGuests.size());

        var availablePremiumRooms = premiumPreBookedRooms.roomCount() - premiumGuests.size();
        logger.debug("[ROOMALLOCATOR] {} premium rooms are free", availablePremiumRooms);

        var availableEconomyRooms = economyPrebookedRooms.roomCount() - economyGuests.size();
        logger.debug("[ROOMALLOCATOR] {} economy rooms are free", availableEconomyRooms);

        if (availablePremiumRooms > 0 && availableEconomyRooms < 0) {
            logger.info("[ROOMALLOCATOR] {} economy guests overbooked", -availableEconomyRooms);
            var economyGuestsForUpgrade = Math.min(availablePremiumRooms, -availableEconomyRooms);
            logger.info("[ROOMALLOCATOR] {} economy guests can be upgraded", economyGuestsForUpgrade);
            premiumGuests = new ArrayList<>(premiumGuests);
            premiumGuests.addAll(economyGuests.subList(0, economyGuestsForUpgrade));
            economyGuests = economyGuests.subList(economyGuestsForUpgrade, economyGuests.size());
        }

        if (economyGuests.size() > availableEconomyRooms) {
            logger.info("[ROOMALLOCATOR] there are more economy guests than economy rooms");
            economyGuests = economyGuests.subList(0, economyPrebookedRooms.roomCount());
        }

        var premiumRevenue = premiumGuests.stream()
                .map(Guest::offerPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var premiumRoomMetrics = new RoomsAllocationMetrics(RoomType.PREMIUM, premiumGuests.size(), premiumRevenue);

        var economyRevenue = economyGuests.stream()
                .map(Guest::offerPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var economyRoomsMetrics = new RoomsAllocationMetrics(RoomType.ECONOMY, economyGuests.size(), economyRevenue);
        logger.info("[DELETE] {} economy room metrics", economyRoomsMetrics);

        return new RoomsAllocationResult(List.of(premiumRoomMetrics, economyRoomsMetrics));
    }
}
