package com.Events.Tickets.infraestructura.adapters.in.web.mappers;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class DateTimeMapper {

    // Conversión de Dominio/Entidad (LocalDateTime) a Instant (si el DTO lo usa)
    public Instant map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        // Convierte LocalDateTime a Instant. Asumimos UTC o un offset fijo.
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    // Conversión inversa de Instant a LocalDateTime (si es necesario para DTO -> Dominio)
    public LocalDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Convierte Instant a LocalDateTime. Asumimos UTC.
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}