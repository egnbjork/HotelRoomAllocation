package com.github.egnbjork.hotelroomallocation.adapters.mappers;

import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import com.github.egnbjork.hotelroomallocation.domain.model.*;
import com.github.egnbjork.hotelroomallocation.infrastructure.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RoomsAllocationDataMapperTest {

    private RoomsAllocationDataMapper mapper;

    @BeforeEach
    void setUp() {
        EnvironmentVariables env = new EnvironmentVariables(new BigDecimal("100"));
        mapper = new RoomsAllocationDataMapper(env);
    }

    @Test
    @DisplayName("should correctly map request to list of available rooms")
    void shouldMapRequestToAvailableRooms() {
        RoomsAllocationRequest request = new RoomsAllocationRequest(3, 2, List.of());

        List<AvailableRooms> result = mapper.toAvailableRooms(request);

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(r -> r.roomType() == RoomType.PREMIUM && r.roomCount() == 3);
        assertThat(result).anyMatch(r -> r.roomType() == RoomType.ECONOMY && r.roomCount() == 2);
    }

    @Test
    @DisplayName("should correctly map request to list of guests")
    void shouldMapRequestToGuests() {
        List<BigDecimal> guestOffers = List.of(
                new BigDecimal("50"), new BigDecimal("100"), new BigDecimal("200")
        );
        RoomsAllocationRequest request = new RoomsAllocationRequest(0, 0, guestOffers);

        List<Guest> guests = mapper.toGuests(request);

        assertThat(guests).hasSize(3);
        assertThat(guests.get(0).isPremium()).isFalse();
        assertThat(guests.get(1).isPremium()).isTrue();
        assertThat(guests.get(2).isPremium()).isTrue();
    }

    @Test
    @DisplayName("should correctly map domain result to response dto")
    void shouldMapDomainResultToResponse() {
        RoomsAllocationMetrics premium = new RoomsAllocationMetrics(RoomType.PREMIUM, 3, new BigDecimal("738"));
        RoomsAllocationMetrics economy = new RoomsAllocationMetrics(RoomType.ECONOMY, 3, new BigDecimal("167.99"));
        RoomsAllocationResult result = new RoomsAllocationResult(List.of(premium, economy));

        RoomsAllocationResponse response = mapper.toRoomAllocationResponse(result);

        assertThat(response.usagePremium()).isEqualTo(3);
        assertThat(response.revenuePremium()).isEqualByComparingTo("738");
        assertThat(response.usageEconomy()).isEqualTo(3);
        assertThat(response.revenueEconomy()).isEqualByComparingTo("167.99");
    }

    @Test
    @DisplayName("should correctly handle missing properties")
    void shouldHandleMissingProperties() {
        RoomsAllocationResult result = new RoomsAllocationResult(List.of());

        RoomsAllocationResponse response = mapper.toRoomAllocationResponse(result);

        assertThat(response.usagePremium()).isZero();
        assertThat(response.revenuePremium()).isEqualByComparingTo("0.00");
        assertThat(response.usageEconomy()).isZero();
        assertThat(response.revenueEconomy()).isEqualByComparingTo("0.00");
    }
}
