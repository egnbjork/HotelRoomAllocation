package com.github.egnbjork.hotelroomallocation.adapters;

import com.github.egnbjork.hotelroomallocation.adapters.mappers.RoomsAllocationDataMapper;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import org.springframework.stereotype.Service;

@Service
public class RoomsAllocationService {
    private final RoomsAllocationDataMapper mapper;
    private final RoomsAllocatorFacade roomsAllocatorFacade;

    public RoomsAllocationService(RoomsAllocationDataMapper mapper,
                                  RoomsAllocatorFacade roomsAllocatorFacade) {
        this.mapper = mapper;
        this.roomsAllocatorFacade = roomsAllocatorFacade;
    }

    public RoomsAllocationResponse allocateRooms(RoomsAllocationRequest request) {
        var availableRooms = mapper.toAvailableRooms(request);
        var guests = mapper.toGuests(request);
        var result = roomsAllocatorFacade.allocateRooms(availableRooms, guests);
        return mapper.toRoomAllocationResponse(result);
    }
}
