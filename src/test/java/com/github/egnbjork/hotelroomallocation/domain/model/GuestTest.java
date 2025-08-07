package com.github.egnbjork.hotelroomallocation.domain.model;

import com.github.egnbjork.hotelroomallocation.domain.model.exception.GuestInitialisationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GuestTest {

    @Test
    @DisplayName("should return true when offerPrice is greater than or equal to threshold")
    void testIsPremiumTrue() {
        Guest guest1 = new Guest(new BigDecimal("100.00"), new BigDecimal("100.00"));
        Guest guest2 = new Guest(new BigDecimal("100.00"), new BigDecimal("150.00"));

        assertTrue(guest1.isPremium(), "Should be premium when offerPrice equals threshold");
        assertTrue(guest2.isPremium(), "Should be premium when offerPrice exceeds threshold");
    }

    @Test
    @DisplayName("should return false when offerPrice is less than threshold")
    void testIsPremiumFalse() {
        Guest guest = new Guest(new BigDecimal("100.00"), new BigDecimal("99.99"));

        assertFalse(guest.isPremium(), "Should not be premium when offerPrice is below threshold");
    }


    @Test
    @DisplayName("should fail if price is null")
    void shouldThrowExceptionWhenOfferPriceIsNull() {
        BigDecimal threshold = new BigDecimal("100");

        GuestInitialisationException exception = assertThrows(GuestInitialisationException.class, () -> new Guest(threshold, null));

        assertEquals("offerPrice must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("should fail if price is negative")
    void shouldThrowExceptionWhenOfferPriceIsNegative() {
        BigDecimal threshold = new BigDecimal("100");
        BigDecimal offerPrice = new BigDecimal("-100");

        GuestInitialisationException exception = assertThrows(GuestInitialisationException.class, () -> new Guest(threshold, offerPrice));

        assertEquals("offerPrice should be positive", exception.getMessage());
    }

    @Test
    @DisplayName("should fail if threshold is null")
    void shouldThrowExceptionWhenThresholdIsNull() {
        BigDecimal offerPrice = new BigDecimal("150");

        GuestInitialisationException exception = assertThrows(GuestInitialisationException.class, () -> new Guest(null, offerPrice));

        assertEquals("threshold must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("should fail if threshold is negative")
    void shouldThrowExceptionWhenThresholdIsNegative() {
        BigDecimal threshold = new BigDecimal("-100");
        BigDecimal offerPrice = new BigDecimal("100");

        GuestInitialisationException exception = assertThrows(GuestInitialisationException.class, () -> new Guest(threshold, offerPrice));

        assertEquals("threshold should be positive", exception.getMessage());
    }
}
