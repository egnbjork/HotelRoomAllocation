package com.github.egnbjork.hotelroomallocation.adapters;

import com.github.egnbjork.hotelroomallocation.domain.RoomAllocator;
import com.github.egnbjork.hotelroomallocation.domain.model.AvailableRooms;
import com.github.egnbjork.hotelroomallocation.domain.model.Guest;
import com.github.egnbjork.hotelroomallocation.domain.model.RoomsAllocationResult;
import com.github.egnbjork.hotelroomallocation.domain.ports.AllocateRooms;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomAllocatorFacade {

    private final AllocateRooms allocateRooms;

    public RoomAllocatorFacade() {
        allocateRooms = new RoomAllocator();
    }

    public RoomsAllocationResult allocateRooms(List<AvailableRooms> availableRooms, List<Guest> guests) {
        return allocateRooms.handle(availableRooms, guests);
    }
}
