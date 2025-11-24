package com.Events.Tickets.infraestructura.adapters.in.web.mappers;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.EventRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.EventResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VenueWebMapper.class, DateTimeMapper.class})
public interface EventWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    Event toModel(EventRequestDTO dto);

    @Mapping(source = "venue", target = "venue")
    EventResponseDTO toResponseDto(Event domainModel);
}
