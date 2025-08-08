package com.github.egnbjork.hotelroomallocation.application;

import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomsAllocationController {

    private final RoomsAllocationService service;

    public RoomsAllocationController(RoomsAllocationService service) {
        this.service = service;
    }

    @PostMapping("/occupancy")
    public ResponseEntity<RoomsAllocationResponse> allocateRooms(
            @Valid
            @RequestBody
            RoomsAllocationRequest request
    ) {
        RoomsAllocationResponse response = service.allocateRooms(request);
        return ResponseEntity.ok(response);
    }
}
