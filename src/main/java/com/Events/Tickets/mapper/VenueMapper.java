package com.Events.Tickets.mapper;

import com.Events.Tickets.dto.request.VenueRequestDTO;
import com.Events.Tickets.dto.response.VenueResponseDTO;
import com.Events.Tickets.entity.VenueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    VenueEntity toEntity(VenueRequestDTO dto);
    VenueResponseDTO toResponseDTO(VenueEntity entity);
}
