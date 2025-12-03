package com.Events.Tickets.infraestructura.adapters.out.jpa.repository;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
