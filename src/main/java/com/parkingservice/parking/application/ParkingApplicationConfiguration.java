package com.parkingservice.parking.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ParkingApplicationConfiguration {

    /** Creates the provider-independent nearby parking use case. */
    @Bean
    FindNearbyParkings findNearbyParkings(ParkingProvider parkingProvider) {
        return new FindNearbyParkings(parkingProvider);
    }
}
