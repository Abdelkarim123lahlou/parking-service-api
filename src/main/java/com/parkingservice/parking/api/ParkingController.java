package com.parkingservice.parking.api;

import com.parkingservice.parking.application.FindNearbyParkings;
import com.parkingservice.parking.domain.GeoPoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for proximity-based parking searches.
 */
@RestController
@RequestMapping(path = "/api/v1/parkings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Parkings", description = "Recherche de parkings et disponibilité en temps réel")
public class ParkingController {

    private final FindNearbyParkings findNearbyParkings;

    /**
     * Creates the HTTP adapter for the nearby parking use case.
     *
     * @param findNearbyParkings proximity search use case
     */
    public ParkingController(FindNearbyParkings findNearbyParkings) {
        this.findNearbyParkings = findNearbyParkings;
    }

    /**
     * Returns geolocated parking facilities around a user position.
     *
     * @param latitude user latitude in WGS84 decimal degrees
     * @param longitude user longitude in WGS84 decimal degrees
     * @param radiusMeters maximum search distance in meters
     * @return matching parkings ordered by distance
     */
    @GetMapping
    @Operation(
            summary = "Rechercher les parkings à proximité",
            description = "Retourne les parkings géolocalisés dans le rayon demandé, triés par distance croissante."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste normalisée des parkings proches",
                    content = @Content(schema = @Schema(implementation = NearbyParkingsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Coordonnées ou rayon invalides",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Fournisseur de données temporairement indisponible",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public NearbyParkingsResponse findNearby(
            @Parameter(description = "Latitude WGS84", example = "46.5802", required = true)
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @Parameter(description = "Longitude WGS84", example = "0.3404", required = true)
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @Parameter(description = "Rayon de recherche en mètres", example = "5000")
            @RequestParam(defaultValue = "5000") @Min(1) @Max(50_000) int radiusMeters
    ) {
        var nearbyParkingResponses = findNearbyParkings.execute(new GeoPoint(latitude, longitude), radiusMeters)
                .stream()
                .map(ParkingResponse::from)
                .toList();
        return new NearbyParkingsResponse(nearbyParkingResponses.size(), nearbyParkingResponses);
    }
}
