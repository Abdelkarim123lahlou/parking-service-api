package com.parkingservice.parking.application;

import com.parkingservice.parking.domain.GeoPoint;

import java.util.Comparator;
import java.util.List;

/**
 * Finds geolocated parkings within a radius and orders them by proximity.
 */
public class FindNearbyParkings {

    private final ParkingProvider parkingProvider;

    /**
     * Creates the use case with the provider selected by application configuration.
     *
     * @param parkingProvider normalized parking data source
     */
    public FindNearbyParkings(ParkingProvider parkingProvider) {
        this.parkingProvider = parkingProvider;
    }

    /**
     * Searches the latest provider snapshot around an origin.
     *
     * @param origin center of the search
     * @param radiusMeters inclusive search radius in meters
     * @return parkings ordered by distance, then by name
     */
    public List<NearbyParking> execute(GeoPoint origin, int radiusMeters) {
        return parkingProvider.fetchAllParkings().stream()
                .filter(parking -> parking.location() != null)
                .map(parking -> new NearbyParking(
                        parking,
                        Math.round(origin.distanceTo(parking.location()))))
                .filter(nearbyParking -> nearbyParking.distanceMeters() <= radiusMeters)
                .sorted(Comparator.comparingLong(NearbyParking::distanceMeters)
                        .thenComparing(nearbyParking -> nearbyParking.parking().name()))
                .toList();
    }
}
