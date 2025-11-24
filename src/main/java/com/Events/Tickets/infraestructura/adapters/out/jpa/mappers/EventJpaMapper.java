package com.Events.Tickets.infraestructura.adapters.out.jpa.mappers;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VenueJpaMapper.class})
public interface EventJpaMapper {

    // Transormamos de dominio a entidad

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EventEntity toEntity(Event domainModel);

    // de entidad a dominio
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    Event toDomain(EventEntity entity);
}