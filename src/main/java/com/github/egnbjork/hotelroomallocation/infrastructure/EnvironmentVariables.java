package com.github.egnbjork.hotelroomallocation.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EnvironmentVariables {
    public BigDecimal premiumPriceThreshold;

    public EnvironmentVariables(
            @Value("${hotel.room-types.premium-price-threshold}") BigDecimal premiumPriceThreshold) {
        this.premiumPriceThreshold = premiumPriceThreshold;
    }
}
