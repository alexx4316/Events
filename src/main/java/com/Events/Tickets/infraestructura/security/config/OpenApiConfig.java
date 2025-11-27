package com.Events.Tickets.infraestructura.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Gestión de Tickets y Eventos", // Título ajustado
                version = "1.0",
                description = "Documentación de la API para la gestión de tickets y eventos.",
                contact = @Contact(
                        name = "Soporte",
                        email = "soporte@events.com",
                        url = "https://events.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        description = "Servidor Local",
                        url = "http://localhost:8080"
                )
        },
        // Esto aplica la seguridad globalmente a todos los endpoints
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
// Esta anotación define el esquema de seguridad que aparecerá en el botón "Authorize"
@SecurityScheme(
        name = "bearerAuth",
        description = "Autenticación JWT. Ingrese el token en el formato: Bearer {token}",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}