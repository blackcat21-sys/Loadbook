package com.cargopro.loadbooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                    new Server().url("http://localhost:" + serverPort).description("Local Development Server"),
                    new Server().url("https://api.cargopro.ai").description("Production Server")
                ))
                .info(new Info()
                        .title("Load & Booking Management System API")
                        .version("1.0.0")
                        .description("REST API for managing loads and bookings in a cargo transportation system. " +
                                   "This system provides comprehensive load management with status transitions, " +
                                   "booking workflows, and business rule enforcement.")
                        .contact(new Contact()
                                .name("CargoPro Development Team")
                                .email("careers@cargopro.ai")
                                .url("https://cargopro.ai"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}