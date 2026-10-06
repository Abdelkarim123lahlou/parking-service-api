package com.parkingservice.parking.domain;

import java.time.Instant;

/**
 * Provider-independent representation of a parking facility and its availability.
 *
 * @param id stable identifier supplied by the parking provider
 * @param name display name
 * @param location geographic position, or {@code null} when the provider has none
 * @param capacity total number of spaces
 * @param availableSpaces number of currently available spaces
 * @param updatedAt time at which the provider last updated the availability
 */
public record Parking(
        String id,
        String name,
        GeoPoint location,
        int capacity,
        int availableSpaces,
        Instant updatedAt
) {
    /** Validates the identity, capacity and availability invariants. */
    public Parking {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Parking id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Parking name is required");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        if (availableSpaces < 0 || availableSpaces > capacity) {
            throw new IllegalArgumentException("Available spaces must be between 0 and capacity");
        }
    }

    /**
     * Calculates the occupied share of the parking capacity.
     *
     * @return occupancy percentage between 0 and 100
     */
    public double occupancyRate() {
        if (capacity == 0) {
            return 0;
        }
        return 100.0 * (capacity - availableSpaces) / capacity;
    }
}
