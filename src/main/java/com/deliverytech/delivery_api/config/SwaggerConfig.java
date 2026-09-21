package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {


        @Value("${server.port:8080}")
        private String serverPort;

        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .info(apiInfo())
                    .servers(List.of(
                        new Server()
                            .url("http://localhost:" + serverPort)
                            .description("Servidor de Desenvolvimento"),
                        new Server()
                            .url("https://api.deliverytech.com")
                            .description("Servidor de Produção")
                    ))
                    .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                    .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
        }

        private Info apiInfo() {
            return new Info()
                    .title("Delivery API")
                    .description("API de gerenciamento de entregas")
                    .version("1.0.0")
                    .contact(new Contact().name("Delivery Tech"))
                    .license(new License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0"));
        }

        private SecurityScheme createAPIKeyScheme() {
                return new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme("bearer")
                        .description("Insira o token JWT obtido no endpoint /api/auth/login");
                }
}
