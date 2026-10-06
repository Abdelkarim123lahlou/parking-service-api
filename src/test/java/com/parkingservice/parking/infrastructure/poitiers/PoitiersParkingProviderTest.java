package com.parkingservice.parking.infrastructure.poitiers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PoitiersParkingProviderTest {

    private MockRestServiceServer server;
    private PoitiersParkingProvider provider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        var properties = new PoitiersProviderProperties(
                URI.create("https://example.test"), "/parkings", Duration.ofSeconds(1), Duration.ofSeconds(1));
        provider = new PoitiersParkingProvider(builder.baseUrl("https://example.test").build(), properties);
    }

    @Test
    void mapsProviderFieldsAndKeepsRecordsWithoutCoordinatesForTheDomain() {
        server.expect(once(), requestTo("https://example.test/parkings?size=100"))
                .andRespond(withSuccess("""
                        {
                          "total": 2,
                          "results": [
                            {
                              "Id": 3,
                              "Nom": "THEATRE",
                              "Capacite": 320,
                              "Places": 106,
                              "_geopoint": "46.5838, 0.3378",
                              "Dernière_mise_à_jour_Base": "2026-10-05T20:24:07Z"
                            },
                            {
                              "Id": 5,
                              "Nom": "GARE EFFIA",
                              "Capacite": 480,
                              "Places": 300
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        var result = provider.fetchAllParkings();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().name()).isEqualTo("THEATRE");
        assertThat(result.getFirst().location().latitude()).isEqualTo(46.5838);
        assertThat(result.get(1).location()).isNull();
        server.verify();
    }

    @Test
    void ignoresARecordWhoseAvailabilityIsInconsistent() {
        server.expect(once(), requestTo("https://example.test/parkings?size=100"))
                .andRespond(withSuccess("""
                        {"results":[{"Id":3,"Nom":"THEATRE","Capacite":10,"Places":11}]}
                        """, MediaType.APPLICATION_JSON));

        assertThat(provider.fetchAllParkings()).isEmpty();
        server.verify();
    }
}
