package com.parkingservice.parking.infrastructure.poitiers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Transport model matching the Grand Poitiers Data Fair response.
 *
 * @param results provider-specific parking records
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record PoitiersParkingResponse(List<PoitiersParkingRecord> results) {

    /** Provider-specific fields required by the normalization adapter. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record PoitiersParkingRecord(
            @JsonProperty("Id") Integer id,
            @JsonProperty("Nom") String name,
            @JsonProperty("Capacite") Integer capacity,
            @JsonProperty("Places") Integer availableSpaces,
            @JsonProperty("_geopoint") String geoPoint,
            @JsonProperty("Dernière_mise_à_jour_Base") Instant updatedAt
    ) {
    }
}
