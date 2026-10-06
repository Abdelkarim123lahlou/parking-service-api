package com.parkingservice.parking.infrastructure.poitiers;

import com.parkingservice.parking.application.ParkingProvider;
import com.parkingservice.parking.domain.GeoPoint;
import com.parkingservice.parking.domain.Parking;
import com.parkingservice.parking.infrastructure.UpstreamServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

/**
 * Retrieves Grand Poitiers Data Fair records and maps them to the domain model.
 */
@Component
@ConditionalOnProperty(name = "parking.provider", havingValue = "poitiers")
public class PoitiersParkingProvider implements ParkingProvider {

    /** Logger used to report rejected provider records without stopping the complete feed. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoitiersParkingProvider.class);

    /** Maximum number of records requested from the Grand Poitiers dataset in one call. */
    private static final int MAX_PROVIDER_RECORDS = 100;

    private final RestClient restClient;
    private final String datasetPath;

    /**
     * Creates the provider adapter with its dedicated HTTP client and dataset settings.
     *
     * @param restClient HTTP client restricted to the configured provider base URL
     * @param providerProperties Grand Poitiers provider settings
     */
    public PoitiersParkingProvider(
            @Qualifier("poitiersRestClient") RestClient restClient,
            PoitiersProviderProperties providerProperties
    ) {
        this.restClient = restClient;
        this.datasetPath = providerProperties.datasetPath();
    }

    /**
     * Fetches and validates the latest Grand Poitiers parking snapshot.
     *
     * @return normalized valid parking records
     * @throws UpstreamServiceException when the remote API cannot provide a usable response
     */
    @Override
    public List<Parking> fetchAllParkings() {
        try {
            PoitiersParkingResponse providerResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path(datasetPath)
                            .queryParam("size", MAX_PROVIDER_RECORDS)
                            .build())
                    .retrieve()
                    .body(PoitiersParkingResponse.class);

            if (providerResponse == null || providerResponse.results() == null) {
                throw new UpstreamServiceException("Grand Poitiers returned an empty response");
            }

            return providerResponse.results().stream()
                    .map(this::mapToDomainParking)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (RestClientException exception) {
            throw new UpstreamServiceException("Grand Poitiers parking data is unavailable", exception);
        }
    }

    private Optional<Parking> mapToDomainParking(PoitiersParkingResponse.PoitiersParkingRecord providerRecord) {
        try {
            if (providerRecord.id() == null || providerRecord.name() == null
                    || providerRecord.capacity() == null || providerRecord.availableSpaces() == null) {
                throw new IllegalArgumentException("required field is missing");
            }
            return Optional.of(new Parking(
                    providerRecord.id().toString(),
                    providerRecord.name().strip(),
                    parseGeoPoint(providerRecord.geoPoint()).orElse(null),
                    providerRecord.capacity(),
                    providerRecord.availableSpaces(),
                    providerRecord.updatedAt()));
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Ignoring invalid parking record with id {}: {}",
                    providerRecord.id(), exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<GeoPoint> parseGeoPoint(String rawGeoPoint) {
        if (rawGeoPoint == null || rawGeoPoint.isBlank()) {
            return Optional.empty();
        }
        String[] coordinateParts = rawGeoPoint.split(",", -1);
        if (coordinateParts.length != 2) {
            throw new IllegalArgumentException("invalid geolocation");
        }
        try {
            return Optional.of(new GeoPoint(
                    Double.parseDouble(coordinateParts[0].strip()),
                    Double.parseDouble(coordinateParts[1].strip())));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("invalid geolocation", exception);
        }
    }
}
