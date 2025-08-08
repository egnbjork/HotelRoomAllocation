package com.github.egnbjork.hotelroomallocation.domain.ports;

import com.github.egnbjork.hotelroomallocation.domain.RoomAllocator;
import com.github.egnbjork.hotelroomallocation.domain.model.*;
import com.github.egnbjork.hotelroomallocation.domain.model.exception.AvailableRoomsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AllocateRoomsTest {

    AllocateRooms allocateRooms = new RoomAllocator();

    @Test
    @DisplayName("should assign premium guests only to premium rooms and calculate correct revenue")
    void shouldAssignPremiumGuests() {
        var premiumRooms = new AvailableRooms(RoomType.PREMIUM, 5);
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 10);
        var availableRooms = List.of(premiumRooms, economyRooms);
        var premiumGuestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal("100.5")),
                new Guest(new BigDecimal(100), new BigDecimal("199.5")),
                new Guest(new BigDecimal(100), new BigDecimal("200"))
        );

        RoomsAllocationResult result = allocateRooms.handle(availableRooms, premiumGuestList);

        Map<RoomType, RoomsAllocationMetrics> premiumRoomMetricsMap = result
                .allocationMetrics()
                .stream()
                .collect(Collectors
                        .toMap(
                                RoomsAllocationMetrics::roomType,
                                Function.identity()
                        ));
        assertEquals(3, premiumRoomMetricsMap.get(RoomType.PREMIUM).usageCount());
        assertEquals(0, premiumRoomMetricsMap.get(RoomType.ECONOMY).usageCount());
        assertEquals(new BigDecimal("500.0"), premiumRoomMetricsMap.get(RoomType.PREMIUM).revenue());
        assertEquals(new BigDecimal("0"), premiumRoomMetricsMap.get(RoomType.ECONOMY).revenue());
    }

    @Test
    @DisplayName("should assign economy guests to economy rooms and calculate proper revenue")
    void shouldAssignEconomy() {
        var premiumRooms = new AvailableRooms(RoomType.PREMIUM, 5);
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 10);
        var availableRooms = List.of(premiumRooms, economyRooms);
        var premiumGuestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal("10.5")),
                new Guest(new BigDecimal(100), new BigDecimal("19.5")),
                new Guest(new BigDecimal(100), new BigDecimal("20"))
        );

        RoomsAllocationResult result = allocateRooms.handle(availableRooms, premiumGuestList);

        Map<RoomType, RoomsAllocationMetrics> premiumRoomMetricsMap = result
                .allocationMetrics()
                .stream()
                .collect(Collectors
                        .toMap(
                                RoomsAllocationMetrics::roomType,
                                Function.identity()
                        ));
        assertEquals(0, premiumRoomMetricsMap.get(RoomType.PREMIUM).usageCount());
        assertEquals(3, premiumRoomMetricsMap.get(RoomType.ECONOMY).usageCount());
        assertEquals(new BigDecimal("0"), premiumRoomMetricsMap.get(RoomType.PREMIUM).revenue());
        assertEquals(new BigDecimal("50.0"), premiumRoomMetricsMap.get(RoomType.ECONOMY).revenue());
    }

    @Test
    @DisplayName("should upgrade economy guests if economy rooms are filled")
    void shouldUpgradeEconomy() {

    }

    @Test
    @DisplayName("should upgrade economy guests to premium if there are available premium rooms")
    void shouldUpgradeEconomyIfPremiumAvailable() {

    }

    @Test
    @DisplayName("should upgrade economy guests in descending order of willingness to pay")
    void shouldUpgradeEconomyInDescendingOrderOfWillingness() {

    }

    // TODO: what to expect if there are no premium rooms provided
    @Test
    @DisplayName("should throw if premium rooms are not found")
    void shouldThrowIfPremiumRoomsAreNotFound() {
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 10);
        List<AvailableRooms> availableRoomsList = List.of(economyRooms);
        var guestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal(100))
        );

        AvailableRoomsException exception = assertThrows(
                AvailableRoomsException.class,
                () -> allocateRooms.handle(availableRoomsList, guestList)
        );

        assertEquals("there are no premium rooms provided", exception.getMessage());
    }

    // TODO: what to expect if there are more premium guests than rooms available
    @Test
    @DisplayName("should randomly pick premium guests if there are more premium guests than rooms available")
    void morePremiumGuestsThanRoomsAvailable() {
        var premiumRooms = new AvailableRooms(RoomType.PREMIUM, 5);
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 10);
        var availableRooms = List.of(premiumRooms, economyRooms);
        var premiumGuestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal("100.5")),
                new Guest(new BigDecimal(100), new BigDecimal("199.5")),
                new Guest(new BigDecimal(100), new BigDecimal("200")),
                new Guest(new BigDecimal(100), new BigDecimal("200")),
                new Guest(new BigDecimal(100), new BigDecimal("200")),
                new Guest(new BigDecimal(100), new BigDecimal("200")),
                new Guest(new BigDecimal(100), new BigDecimal("200")),
                new Guest(new BigDecimal(100), new BigDecimal("200")),
                new Guest(new BigDecimal(100), new BigDecimal("200"))
        );

        RoomsAllocationResult result = allocateRooms.handle(availableRooms, premiumGuestList);

        assertEquals(1, result.allocationMetrics().size());
        assertEquals(RoomType.PREMIUM, result.allocationMetrics().getFirst().roomType());

        RoomsAllocationMetrics premiumRoomMetrics = result.allocationMetrics().getFirst();
        assertEquals(5, premiumRoomMetrics.usageCount());
    }
}
