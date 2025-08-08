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
        Map<RoomType, AvailableRooms> roomsByType = mapRoomsByType(availableRooms);
        AvailableRooms premiumRooms = getPremiumRooms(roomsByType);
        AvailableRooms economyRooms = roomsByType.getOrDefault(RoomType.ECONOMY, new AvailableRooms(RoomType.ECONOMY, 0));

        List<Guest> premiumGuests = getSortedPremiumGuests(guests);
        List<Guest> economyGuests = getSortedEconomyGuests(guests);

        premiumGuests = limitToAvailableRooms(premiumGuests, premiumRooms.roomCount());

        int freePremiumRoomsCount = premiumRooms.roomCount() - premiumGuests.size();
        int overbookedEconomyRoomsCount = economyGuests.size() - economyRooms.roomCount();
        logger.debug("[ROOMALLOCATOR] {} premium rooms are free", freePremiumRoomsCount);
        logger.debug("[ROOMALLOCATOR] {} economy rooms are free", economyRooms.roomCount() - economyGuests.size());

        if (freePremiumRoomsCount > 0 && overbookedEconomyRoomsCount > 0) {
            int economyForUpgradeGuestCount = Math.min(freePremiumRoomsCount, overbookedEconomyRoomsCount);
            logger.info("[ROOMALLOCATOR] {} economy guests will be upgraded", economyForUpgradeGuestCount);

            List<Guest> upgradedGuests = economyGuests.subList(0, economyForUpgradeGuestCount);
            economyGuests = economyGuests.subList(economyForUpgradeGuestCount, economyGuests.size());

            List<Guest> updatedPremium = new ArrayList<>(premiumGuests);
            updatedPremium.addAll(upgradedGuests);

            premiumGuests = new ArrayList<>(premiumGuests);
            premiumGuests.addAll(updatedPremium);
        }

        economyGuests = limitToAvailableRooms(economyGuests, economyRooms.roomCount());

        RoomsAllocationMetrics premiumMetrics = calculateMetrics(RoomType.PREMIUM, premiumGuests);
        RoomsAllocationMetrics economyMetrics = calculateMetrics(RoomType.ECONOMY, economyGuests);

        return new RoomsAllocationResult(List.of(premiumMetrics, economyMetrics));
    }

    private Map<RoomType, AvailableRooms> mapRoomsByType(List<AvailableRooms> availableRooms) {
        return availableRooms.stream()
                .collect(Collectors.toMap(AvailableRooms::roomType, Function.identity()));
    }

    private AvailableRooms getPremiumRooms(Map<RoomType, AvailableRooms> roomsByType) {
        AvailableRooms premiumRooms = roomsByType.get(RoomType.PREMIUM);
        if (premiumRooms == null) {
            throw new AvailableRoomsException("There are no premium rooms provided");
        }
        return premiumRooms;
    }

    private List<Guest> getSortedPremiumGuests(List<Guest> guests) {
        List<Guest> premiumGuests = guests.stream()
                .filter(Guest::isPremium)
                .sorted(Comparator.comparing(Guest::offerPrice).reversed())
                .toList();
        logger.info("[ROOMALLOCATOR] received {} premium guests", premiumGuests.size());
        return premiumGuests;
    }

    private List<Guest> getSortedEconomyGuests(List<Guest> guests) {
        List<Guest> economyGuests = guests.stream()
                .filter(n -> !n.isPremium())
                .sorted(Comparator.comparing(Guest::offerPrice).reversed())
                .toList();
        logger.info("[ROOMALLOCATOR] received {} economy guests", economyGuests.size());
        return economyGuests;
    }

    private List<Guest> limitToAvailableRooms(List<Guest> guests, int availableRooms) {
        if (guests.size() > availableRooms) {
            return guests.subList(0, availableRooms);
        }
        return guests;
    }

    private RoomsAllocationMetrics calculateMetrics(RoomType roomType, List<Guest> guests) {
        BigDecimal revenue = guests.stream()
                .map(Guest::offerPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RoomsAllocationMetrics(roomType, guests.size(), revenue);
    }
}

