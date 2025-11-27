package com.Events.Tickets.infraestructura.adapters.in.web.dto.response;

import com.Events.Tickets.dominio.enums.EventType;
import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

    private Long id;
    private String name;
    private String description;
    private EventType eventType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Se usa el DTO de respuesta del Venue
    private VenueResponseDTO venue;

    private Instant createdAt;
    private Instant updatedAt;
}