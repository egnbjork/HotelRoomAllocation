package com.github.egnbjork.hotelroomallocation.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomsAllocationController.class)
class RoomsAllocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RoomsAllocationService roomsAllocationService() {
            return Mockito.mock(RoomsAllocationService.class);
        }
    }

    @Test
    @DisplayName("should accept valid request")
    void shouldAcceptValidRequest() throws Exception {
        var request = Map.of(
                "premiumRooms", 2,
                "economyRooms", 2,
                "potentialGuests", List.of(99.99, 120.00)
        );

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should reject negative room values")
    void shouldRejectNegativeRoomValues() throws Exception {
        var request = Map.of(
                "premiumRooms", -1,
                "economyRooms", -5,
                "potentialGuests", List.of(100)
        );

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("should reject zero room values")
    void shouldRejectZeroRoomValues() throws Exception {
        var request = Map.of(
                "premiumRooms", 0,
                "economyRooms", 0,
                "potentialGuests", List.of(100)
        );

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("should reject null values")
    void shouldRejectNullValues() throws Exception {
        String invalidRequest = """
                {
                    "premiumRooms": null,
                    "economyRooms": null,
                    "potentialGuests": null
                }
                """;

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("should reject null guest in list")
    void shouldRejectNullGuestValue() throws Exception {
        String request = """
                {
                        "premiumRooms": 1,
                        "economyRooms": 1,
                        "potentialGuests": [100, null, 200]
                }
                """;

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("should reject empty guest list")
    void shouldRejectEmptyGuestList() throws Exception {
        var request = Map.of(
                "premiumRooms", 1,
                "economyRooms", 1,
                "potentialGuests", List.of()
        );

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("should reject guest with 0 or negative offer")
    void shouldRejectGuestWithZeroOrNegativeOffer() throws Exception {
        var request = Map.of(
                "premiumRooms", 1,
                "economyRooms", 1,
                "potentialGuests", List.of(0, -5)
        );

        mockMvc.perform(post("/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
