package com.Events.Tickets.infraestructura.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private String capacity;

    private Instant createdAt;
    private Instant updatedAt;
}
