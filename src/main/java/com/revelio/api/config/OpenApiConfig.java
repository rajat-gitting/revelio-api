package com.revelio.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Revelio API",
            version = "1.0.0",
            description = "Production-oriented REST API for Revelio platform services."))
public class OpenApiConfig {}
