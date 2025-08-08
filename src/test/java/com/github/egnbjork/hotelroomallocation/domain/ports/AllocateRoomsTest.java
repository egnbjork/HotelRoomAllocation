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

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("should upgrade economy guests if economy rooms are fully booked")
    void shouldUpgradeEconomy() {
        var premiumRooms = new AvailableRooms(RoomType.PREMIUM, 5);
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 2);
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
        assertEquals(1, premiumRoomMetricsMap.get(RoomType.PREMIUM).usageCount());
        assertEquals(2, premiumRoomMetricsMap.get(RoomType.ECONOMY).usageCount());
        assertEquals(new BigDecimal("20"), premiumRoomMetricsMap.get(RoomType.PREMIUM).revenue());
        assertEquals(new BigDecimal("30.0"), premiumRoomMetricsMap.get(RoomType.ECONOMY).revenue());
    }

    @Test
    @DisplayName("should pick top-paying economy guests for upgrade" +
            " if there are not enough premium rooms for all economy+premium guests")
    void shouldUpgradeEconomyIfPremiumAvailable() {
        var premiumRooms = new AvailableRooms(RoomType.PREMIUM, 2);
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 2);
        var availableRooms = List.of(premiumRooms, economyRooms);
        var guestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal("10.5")),
                new Guest(new BigDecimal(100), new BigDecimal("19.5")),
                new Guest(new BigDecimal(100), new BigDecimal("20")),
                new Guest(new BigDecimal(100), new BigDecimal("10.5")),
                new Guest(new BigDecimal(100), new BigDecimal("19.5")),
                new Guest(new BigDecimal(100), new BigDecimal("20"))
        );

        RoomsAllocationResult result = allocateRooms.handle(availableRooms, guestList);

        Map<RoomType, RoomsAllocationMetrics> premiumRoomMetricsMap = result
                .allocationMetrics()
                .stream()
                .collect(Collectors
                        .toMap(
                                RoomsAllocationMetrics::roomType,
                                Function.identity()
                        ));
        assertEquals(2, premiumRoomMetricsMap.get(RoomType.PREMIUM).usageCount());
        assertEquals(2, premiumRoomMetricsMap.get(RoomType.ECONOMY).usageCount());
        assertEquals(new BigDecimal("40"), premiumRoomMetricsMap.get(RoomType.PREMIUM).revenue());
        assertEquals(new BigDecimal("39.0"), premiumRoomMetricsMap.get(RoomType.ECONOMY).revenue());
    }

    @Test
    @DisplayName("should not upgrade any economy guests if premium rooms are fully booked and leave top paying ones")
    void shouldNotUpgradeEconomyIfPremiumNotAvailable() {
        var premiumRooms = new AvailableRooms(RoomType.PREMIUM, 1);
        var economyRooms = new AvailableRooms(RoomType.ECONOMY, 2);
        var availableRooms = List.of(premiumRooms, economyRooms);
        var guestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal("10.5")),
                new Guest(new BigDecimal(100), new BigDecimal("19.5")),
                new Guest(new BigDecimal(100), new BigDecimal("20")),
                new Guest(new BigDecimal(100), new BigDecimal("10.5")),
                new Guest(new BigDecimal(100), new BigDecimal("19.5")),
                new Guest(new BigDecimal(100), new BigDecimal("20")),
                new Guest(new BigDecimal(100), new BigDecimal("200"))
        );

        RoomsAllocationResult result = allocateRooms.handle(availableRooms, guestList);

        Map<RoomType, RoomsAllocationMetrics> premiumRoomMetricsMap = result
                .allocationMetrics()
                .stream()
                .collect(Collectors
                        .toMap(
                                RoomsAllocationMetrics::roomType,
                                Function.identity()
                        ));
        assertEquals(1, premiumRoomMetricsMap.get(RoomType.PREMIUM).usageCount());
        assertEquals(2, premiumRoomMetricsMap.get(RoomType.ECONOMY).usageCount());
        assertEquals(new BigDecimal("200"), premiumRoomMetricsMap.get(RoomType.PREMIUM).revenue());
        assertEquals(new BigDecimal("40"), premiumRoomMetricsMap.get(RoomType.ECONOMY).revenue());
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

    // TODO: what to expect if there are no economy rooms provided
    @Test
    @DisplayName("should not fail if no economy rooms provided")
    void shouldNotThrowIfEconomyRoomsAreNotFound() {
        var economyRooms = new AvailableRooms(RoomType.PREMIUM, 10);
        List<AvailableRooms> availableRoomsList = List.of(economyRooms);
        var guestList = List.of(
                new Guest(new BigDecimal(100), new BigDecimal(100))
        );

        assertDoesNotThrow(
                () -> allocateRooms.handle(availableRoomsList, guestList)
        );
    }

    // TODO: what to expect if there are more premium guests than rooms available
    @Test
    @DisplayName("should pick best payed premium guests if there are more premium guests than rooms available")
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

        assertEquals(RoomType.PREMIUM, result.allocationMetrics().getFirst().roomType());

        RoomsAllocationMetrics premiumRoomMetrics = result.allocationMetrics().getFirst();
        assertEquals(5, premiumRoomMetrics.usageCount());
        assertEquals(new BigDecimal("1000"), premiumRoomMetrics.revenue());
    }
}
