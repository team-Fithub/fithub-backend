package com.fithub.fithubbackend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.swagger.url}")
    String url;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("Fithub API Document").version("1.0.0");
        String jwtName = "Authorization";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtName);
        Components components = new Components()
                .addSecuritySchemes(jwtName, new SecurityScheme()
                        .name(jwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addSecurityItem(securityRequirement)
                .components(components)
                .info(info);
    }
}
