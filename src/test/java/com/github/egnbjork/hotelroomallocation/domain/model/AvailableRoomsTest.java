package com.github.egnbjork.hotelroomallocation.domain.model;

import com.github.egnbjork.hotelroomallocation.domain.model.exception.AvailableRoomsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailableRoomsTest {

    @Test
    @DisplayName("should throw when room type is null")
    void shouldThrowExceptionWhenRoomTypeIsNull() {
        AvailableRoomsException exception = assertThrows(
                AvailableRoomsException.class,
                () -> new AvailableRooms(null, 5)
        );

        assertEquals("roomType cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("should throw when room number is negative")
    void shouldThrowExceptionWhenRoomNumberIsNegative() {
        AvailableRoomsException exception = assertThrows(
                AvailableRoomsException.class,
                () -> new AvailableRooms(RoomType.PREMIUM, -1)
        );

        assertEquals("roomCount cannot be negative", exception.getMessage());
    }
}
