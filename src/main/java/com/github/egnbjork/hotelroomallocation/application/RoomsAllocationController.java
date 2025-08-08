package com.github.egnbjork.hotelroomallocation.application;

import com.github.egnbjork.hotelroomallocation.adapters.RoomsAllocationService;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms/")
public class RoomsAllocationController {

    private final RoomsAllocationService service;

    public RoomsAllocationController(RoomsAllocationService service) {
        this.service = service;
    }

    @PostMapping("/allocate")
    public ResponseEntity<RoomsAllocationResponse> allocateRooms(@RequestBody RoomsAllocationRequest request) {
        RoomsAllocationResponse response = service.allocateRooms(request);
        return ResponseEntity.ok(response);
    }
}
