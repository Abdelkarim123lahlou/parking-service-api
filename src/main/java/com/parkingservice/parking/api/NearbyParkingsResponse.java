package com.parkingservice.parking.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Public response returned for a proximity search.
 *
 * @param count number of parkings in the response
 * @param parkings parkings ordered by increasing distance
 */
@Schema(description = "Résultat d'une recherche de parkings à proximité")
public record NearbyParkingsResponse(
        @Schema(description = "Nombre de parkings retournés", example = "7") int count,
        @Schema(description = "Parkings triés par distance croissante") List<ParkingResponse> parkings
) {
}
