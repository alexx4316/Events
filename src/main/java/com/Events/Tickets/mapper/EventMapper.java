package com.Events.Tickets.mapper;

import com.Events.Tickets.dto.request.EventRequestDTO;
import com.Events.Tickets.dto.response.EventResponseDTO;
import com.Events.Tickets.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VenueMapper.class})
public interface EventMapper {

    // 1. DTO de Petición a Entidad (Para crear/actualizar)
    @Mapping(target = "id", ignore = true)
    // Ignoramos el objeto 'venue' de la Entidad. La clave foránea la gestionará el Service,
    // buscando la VenueEntity por el ID que viene en el DTO.
    @Mapping(target = "venue", ignore = true)
    EventEntity toEntity(EventRequestDTO dto);

    // 2. Entidad a DTO de Respuesta
    // MapStruct llama automáticamente a VenueMapper.toResponseDTO(VenueEntity) para llenar el campo 'venue'.
    EventResponseDTO toResponseDTO(EventEntity entity);
}