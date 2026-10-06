package com.parkingservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingServiceApplicationTests {

	private final MockMvc mockMvc;

	@Autowired
	ParkingServiceApplicationTests(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@Test
	void contextLoads() {
	}

	@Test
	void rejectsAnInvalidRadiusAtTheHttpBoundary() throws Exception {
		mockMvc.perform(get("/api/v1/parkings")
					.param("latitude", "46.58")
					.param("longitude", "0.34")
					.param("radiusMeters", "0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void exposesTheOpenApiSpecification() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Parking Service API"))
				.andExpect(jsonPath("$.paths['/api/v1/parkings'].get").exists());
	}

	@Test
	void redirectsTheSwaggerEntryPointToItsUserInterface() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/swagger-ui/index.html"));
	}
}
