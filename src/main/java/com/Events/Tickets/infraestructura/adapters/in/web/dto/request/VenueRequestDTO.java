package com.Events.Tickets.infraestructura.adapters.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestDTO {

    @NotBlank(message = "The name of the venue is mandatory.")
    @Size(max = 100, message = "The name must not exceed 100 characters.")
    private String name;

    @NotBlank(message = "The address is mandatory.")
    private String address;

    @NotBlank(message = "The city is mandatory and is used for filters.")
    @Size(max = 50, message = "The city must not exceed 50 characters.")
    private String city;

    @NotBlank(message = "The country is required.")
    @Size(max = 50, message = "The country must not exceed 50 characters.")
    private String country;

    @NotNull(message = "The capacity of the venue is mandatory.")
    @Min(value = 1, message = "The capacity must be at least 1.")
    private Integer capacity;

}
