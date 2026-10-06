package com.parkingservice.parking.infrastructure.poitiers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

/**
 * Validated server-side settings for the Grand Poitiers data source.
 *
 * @param baseUrl trusted Data Fair server URL
 * @param datasetPath path of the parking dataset
 * @param connectTimeout maximum connection establishment time
 * @param readTimeout maximum response wait time
 */
@Validated
@ConfigurationProperties("parking.providers.poitiers")
public record PoitiersProviderProperties(
        @NotNull URI baseUrl,
        @NotBlank String datasetPath,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {
}
