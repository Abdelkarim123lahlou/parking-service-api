package com.parkingservice.parking.infrastructure.poitiers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

/**
 * Creates the bounded HTTP client dedicated to the Grand Poitiers API.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "parking.provider", havingValue = "poitiers")
class PoitiersHttpConfiguration {

    /**
     * Configures connection and read timeouts for the provider boundary.
     *
     * @param providerProperties trusted server-side provider settings
     * @return HTTP client scoped to the Grand Poitiers base URL
     */
    @Bean("poitiersRestClient")
    RestClient poitiersRestClient(PoitiersProviderProperties providerProperties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(providerProperties.connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(providerProperties.readTimeout());

        return RestClient.builder()
                .baseUrl(providerProperties.baseUrl().toString())
                .requestFactory(requestFactory)
                .build();
    }
}
