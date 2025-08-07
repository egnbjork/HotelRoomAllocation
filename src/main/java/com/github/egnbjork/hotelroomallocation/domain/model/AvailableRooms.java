package com.github.egnbjork.hotelroomallocation.domain.model;

import com.github.egnbjork.hotelroomallocation.domain.model.exception.AvailableRoomsException;

public record AvailableRooms(RoomType roomType, int roomCount) {
    public AvailableRooms(RoomType roomType, int roomCount) {
        this.roomType = roomType;
        if (roomType == null) {
            throw new AvailableRoomsException("roomType cannot be null");
        }
        this.roomCount = roomCount;
        if (roomCount < 0) {
            throw new AvailableRoomsException("roomCount cannot be negative");
        }
    }
}
