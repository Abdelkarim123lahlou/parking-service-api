package com.parkingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Describes the public parking API in the generated OpenAPI specification.
 */
@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

    /** Creates the OpenAPI metadata configuration. */
    public OpenApiConfiguration() {
    }

    /**
     * Builds the metadata displayed by Swagger UI and OpenAPI clients.
     *
     * @return the public API description
     */
    @Bean
    OpenAPI parkingServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Parking Service API")
                .description("Recherche de parkings à proximité avec disponibilité en temps réel.")
                .version("v1"));
    }
}
