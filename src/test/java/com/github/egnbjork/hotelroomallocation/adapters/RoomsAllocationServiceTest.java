package com.github.egnbjork.hotelroomallocation.adapters;

import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class RoomsAllocationServiceTest {
    @Autowired
    RoomsAllocationService service;

    private static final List<BigDecimal> DEFAULT_GUESTS = List.of(
            BigDecimal.valueOf(23),
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(155),
            BigDecimal.valueOf(374),
            BigDecimal.valueOf(22),
            BigDecimal.valueOf(99.99),
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(101),
            BigDecimal.valueOf(115),
            BigDecimal.valueOf(209)
    );

    @Test
    @DisplayName("Should allocate rooms correctly when given 3 premium and 3 economy rooms")
    void readMeTestCase1() {
        RoomsAllocationRequest request = new RoomsAllocationRequest(3, 3, DEFAULT_GUESTS);

        RoomsAllocationResponse response = service.allocateRooms(request);

        assertThat(response.usagePremium()).isEqualTo(3);
        assertThat(response.revenuePremium()).isEqualByComparingTo("738");

        assertThat(response.usageEconomy()).isEqualTo(3);
        assertThat(response.revenueEconomy()).isEqualByComparingTo("167.99");
    }

    @Test
    @DisplayName("Should allocate rooms correctly when given 7 premium and 5 economy rooms")
    void reamMeTestCase2() {
        RoomsAllocationRequest request = new RoomsAllocationRequest(7, 5, DEFAULT_GUESTS);

        RoomsAllocationResponse response = service.allocateRooms(request);

        assertThat(response.usagePremium()).isEqualTo(6);
        assertThat(response.revenuePremium()).isEqualByComparingTo("1054");

        assertThat(response.usageEconomy()).isEqualTo(4);
        assertThat(response.revenueEconomy()).isEqualByComparingTo("189.99");
    }

    @Test
    @DisplayName("Should allocate rooms correctly when given 2 premium and 7 economy rooms")
    void readMeTestCase3() {
        RoomsAllocationRequest request = new RoomsAllocationRequest(2, 7, DEFAULT_GUESTS);

        RoomsAllocationResponse response = service.allocateRooms(request);

        assertThat(response.usagePremium()).isEqualTo(2);
        assertThat(response.revenuePremium()).isEqualByComparingTo("583");

        assertThat(response.usageEconomy()).isEqualTo(4);
        assertThat(response.revenueEconomy()).isEqualByComparingTo("189.99");
    }}
