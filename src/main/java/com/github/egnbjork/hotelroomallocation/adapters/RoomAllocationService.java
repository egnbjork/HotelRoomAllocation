package com.github.egnbjork.hotelroomallocation.adapters;

import com.github.egnbjork.hotelroomallocation.adapters.mappers.RoomsAllocationDataMapper;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import org.springframework.stereotype.Service;

@Service
public class RoomAllocationService {
    private final RoomsAllocationDataMapper mapper;
    private final RoomAllocatorFacade roomAllocatorFacade;

    public RoomAllocationService(RoomsAllocationDataMapper mapper,
                                 RoomAllocatorFacade roomAllocatorFacade) {
        this.mapper = mapper;
        this.roomAllocatorFacade = roomAllocatorFacade;
    }

    public RoomsAllocationResponse allocateRooms(RoomsAllocationRequest request) {
        var availableRooms = mapper.toAvailableRooms(request);
        var guests = mapper.toGuests(request);
        var result = roomAllocatorFacade.allocateRooms(availableRooms, guests);
        return mapper.toRoomAllocationResponse(result);
    }
}
