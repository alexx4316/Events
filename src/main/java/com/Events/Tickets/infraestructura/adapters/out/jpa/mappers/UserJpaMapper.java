package com.Events.Tickets.infraestructura.adapters.out.jpa.mappers;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper {

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
