package com.github.egnbjork.hotelroomallocation.domain.ports;

import com.github.egnbjork.hotelroomallocation.domain.model.AvailableRooms;
import com.github.egnbjork.hotelroomallocation.domain.model.Guest;
import com.github.egnbjork.hotelroomallocation.domain.model.RoomsAllocationResult;

import java.util.List;

public interface AllocateRooms {
    RoomsAllocationResult handle(List<AvailableRooms> availableRooms, List<Guest> guests);
}
