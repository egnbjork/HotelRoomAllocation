package com.github.egnbjork.hotelroomallocation.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.egnbjork.hotelroomallocation.adapters.RoomsAllocationService;
import com.github.egnbjork.hotelroomallocation.application.RoomsAllocationController;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationRequest;
import com.github.egnbjork.hotelroomallocation.application.dto.RoomsAllocationResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomsAllocationController.class)
class RoomsAllocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomsAllocationService service;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RoomsAllocationService roomsAllocationService() {
            return Mockito.mock(RoomsAllocationService.class);
        }
    }

    @Test
    void shouldReturnAllocationResponse() throws Exception {
        RoomsAllocationRequest request = new RoomsAllocationRequest(
                3,
                3,
                List.of(BigDecimal.valueOf(23), BigDecimal.valueOf(45), BigDecimal.valueOf(155))
        );

        RoomsAllocationResponse response = new RoomsAllocationResponse(
                2,
                new BigDecimal("400.00"),
                1,
                new BigDecimal("45.00")
        );

        when(service.allocateRooms(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/rooms/allocate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usagePremium").value(2))
                .andExpect(jsonPath("$.revenuePremium").value(400.00))
                .andExpect(jsonPath("$.usageEconomy").value(1))
                .andExpect(jsonPath("$.revenueEconomy").value(45.00));
    }
}
