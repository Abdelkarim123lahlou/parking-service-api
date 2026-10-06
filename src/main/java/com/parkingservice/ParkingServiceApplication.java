package com.parkingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot entry point and root component-scan boundary.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ParkingServiceApplication {

    /** Creates the root Spring configuration class. */
    public ParkingServiceApplication() {
    }

    /**
     * Starts the parking REST API.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(ParkingServiceApplication.class, args);
    }
}
