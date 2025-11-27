package com.Events.Tickets.infraestructura.adapters.out.jpa.mappers;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserJpaMapper {

    // Conversión de Dominio a Entidad
    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(User domain);

    // Conversión de Entidad a Dominio
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    User toDomain(UserEntity entity);
}
