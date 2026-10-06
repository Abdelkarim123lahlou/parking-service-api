package com.parkingservice.parking.application;

import com.parkingservice.parking.domain.Parking;

/**
 * Parking facility enriched with its distance from the requested position.
 *
 * @param parking normalized parking information
 * @param distanceMeters rounded distance from the search origin
 */
public record NearbyParking(Parking parking, long distanceMeters) {
}
