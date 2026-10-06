package com.parkingservice.parking.api;

import com.parkingservice.parking.application.FindNearbyParkings;
import com.parkingservice.parking.domain.GeoPoint;
import com.parkingservice.parking.domain.Parking;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ParkingControllerTest {

    private final MockMvc mockMvc;

    ParkingControllerTest() {
        var parking = new Parking("3", "THEATRE", new GeoPoint(46.5838, 0.3378),
                320, 106, Instant.parse("2026-10-05T20:24:07Z"));
        var useCase = new FindNearbyParkings(() -> List.of(parking));
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ParkingController(useCase))
                .setValidator(validator)
                .build();
    }

    @Test
    void exposesAStableNormalizedContract() throws Exception {
        mockMvc.perform(get("/api/v1/parkings")
                        .param("latitude", "46.58")
                        .param("longitude", "0.34")
                        .param("radiusMeters", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.parkings[0].id").value("3"))
                .andExpect(jsonPath("$.parkings[0].name").value("THEATRE"))
                .andExpect(jsonPath("$.parkings[0].availableSpaces").value(106))
                .andExpect(jsonPath("$.parkings[0].distanceMeters").isNumber())
                .andExpect(jsonPath("$.parkings[0].location.latitude").value(46.5838));
    }

    @Test
    void rejectsARequestWithoutCoordinates() throws Exception {
        mockMvc.perform(get("/api/v1/parkings")
                        .param("longitude", "0.34")
                        .param("radiusMeters", "1000"))
                .andExpect(status().isBadRequest());
    }
}
