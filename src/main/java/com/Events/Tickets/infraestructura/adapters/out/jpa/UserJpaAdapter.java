package com.Events.Tickets.infraestructura.adapters.out.jpa;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.UserEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.mappers.UserJpaMapper;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserJpaMapper userJpaMapper;
    @Override
    public User save(User user) {

        // Dominio - entidad
        UserEntity entity = userJpaMapper.toEntity(user);

        // Persistencia de datos
        UserEntity savedEntity = userRepository.save(entity);

        // Entidad - dominio
        return userJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userJpaMapper::toDomain);
    }
}
