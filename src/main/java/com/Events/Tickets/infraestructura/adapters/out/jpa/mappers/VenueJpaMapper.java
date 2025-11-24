package com.Events.Tickets.infraestructura.adapters.out.jpa.mappers;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.VenueResponseDTO;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.VenueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueJpaMapper {

    // 1. Dominio -> Entidad (Usado al guardar)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VenueEntity toEntity(Venue domainModel);

    // 2. Entidad -> Dominio (Usado al leer)
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    Venue toDomain(VenueEntity entity);
}
