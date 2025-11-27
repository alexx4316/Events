package com.Events.Tickets.infraestructura.adapters.in.web.dto.request;

import com.Events.Tickets.dominio.enums.EventType;
import com.Events.Tickets.infraestructura.validation.DateRange;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DateRange
public class EventRequestDTO {

    @NotBlank(message = "The event name is required.")
    private String name;

    @NotBlank(message = "The description is required..")
    private String description;

    @NotNull(message = "The type of event is mandatory.")
    private EventType eventType;

    @NotNull(message = "{event.start.date.required}")
    @Future(message = "The start date must be in the future..")
    private LocalDateTime startDate;

    @NotNull(message = "{event.end.date.required}")
    private LocalDateTime endDate;

    @NotNull(message = "The venue ID is required...")
    @Min(value = 1, message = "The venue ID must be a positive value.")
    private Long venueId;
}