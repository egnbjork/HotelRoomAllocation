package com.github.egnbjork.hotelroomallocation.domain.model;

import com.github.egnbjork.hotelroomallocation.domain.model.exception.GuestInitialisationException;

import java.math.BigDecimal;

public record Guest(BigDecimal threshold, BigDecimal offerPrice) {
    public Guest {
        if (threshold == null) {
            throw new GuestInitialisationException("threshold must not be null");
        }
        if (threshold.compareTo(BigDecimal.ZERO) < 0) {
            throw new GuestInitialisationException("threshold should be positive");
        }
        if (offerPrice == null) {
            throw new GuestInitialisationException("offerPrice must not be null");
        }
        if (offerPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new GuestInitialisationException("offerPrice should be positive");
        }
    }

    public boolean isPremium() {
        return offerPrice.compareTo(threshold) >= 0;
    }
}
