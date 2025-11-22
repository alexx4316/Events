package com.Events.Tickets.dto.request;

import com.Events.Tickets.entity.EventType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {

    @NotBlank(message = "The event name is required.")
    private String name;

    @NotBlank(message = "The description is required..")
    private String description;

    @NotNull(message = "The type of event is mandatory.")
    private EventType eventType;

    @NotNull(message = "The start date is required.")
    @Future(message = "The start date must be in the future..")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "The venue ID is required...")
    @Min(value = 1, message = "The venue ID must be a positive value.")
    private Long venueId;
}