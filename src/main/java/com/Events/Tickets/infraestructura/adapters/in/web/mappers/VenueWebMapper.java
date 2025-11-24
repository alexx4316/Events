package com.Events.Tickets.infraestructura.adapters.in.web.mappers;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.VenueResponseDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.VenueRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface VenueWebMapper {

    // 1. Convierte el DTO de entrada al Modelo de Dominio.
    Venue toModel(VenueRequestDTO requestDTO);

    // 2. convierte el Model al DTO
    VenueResponseDTO toResponseDto(Venue venue);

}
