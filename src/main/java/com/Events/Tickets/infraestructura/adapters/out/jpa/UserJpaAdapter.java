package com.Events.Tickets.infraestructura.adapters.out.jpa;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.UserEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.mappers.UserJpaMapper;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserRepository repository;
    private final UserJpaMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
