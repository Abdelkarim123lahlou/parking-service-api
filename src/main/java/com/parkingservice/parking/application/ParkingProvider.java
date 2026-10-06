package com.parkingservice.parking.application;

import com.parkingservice.parking.domain.Parking;

import java.util.List;

/**
 * Port implemented by each city-specific or vendor-specific parking data source.
 */
public interface ParkingProvider {

    /**
     * Loads the latest normalized parking information available from the provider.
     *
     * @return immutable snapshot of known parkings
     */
    List<Parking> fetchAllParkings();
}
