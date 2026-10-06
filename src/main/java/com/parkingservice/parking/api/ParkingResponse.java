package com.parkingservice.parking.api;

import com.parkingservice.parking.application.NearbyParking;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Stable public representation of parking availability.
 *
 * @param id provider-independent API identifier
 * @param name parking display name
 * @param location geographic position
 * @param capacity total number of spaces
 * @param availableSpaces currently available spaces
 * @param occupancyRate occupied percentage
 * @param distanceMeters distance from the requested position
 * @param updatedAt last provider update time
 */
@Schema(description = "Parking normalisé avec disponibilité et distance")
public record ParkingResponse(
        @Schema(description = "Identifiant du parking", example = "3") String id,
        @Schema(description = "Nom affiché", example = "THEATRE") String name,
        @Schema(description = "Coordonnées WGS84") LocationResponse location,
        @Schema(description = "Capacité totale", example = "320") int capacity,
        @Schema(description = "Nombre de places disponibles", example = "106") int availableSpaces,
        @Schema(description = "Taux d'occupation en pourcentage", example = "66.875") double occupancyRate,
        @Schema(description = "Distance depuis la position recherchée, en mètres", example = "451") long distanceMeters,
        @Schema(description = "Dernière actualisation de la source", example = "2026-10-05T20:24:07Z") Instant updatedAt
) {
    /** Maps an application result to the stable HTTP response model. */
    static ParkingResponse from(NearbyParking nearbyParking) {
        var parking = nearbyParking.parking();
        return new ParkingResponse(
                parking.id(),
                parking.name(),
                new LocationResponse(parking.location().latitude(), parking.location().longitude()),
                parking.capacity(),
                parking.availableSpaces(),
                parking.occupancyRate(),
                nearbyParking.distanceMeters(),
                parking.updatedAt());
    }

    /**
     * Public WGS84 position.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     */
    @Schema(description = "Position géographique WGS84")
    public record LocationResponse(
            @Schema(example = "46.58383455409422") double latitude,
            @Schema(example = "0.33779491061805567") double longitude
    ) {
    }
}
