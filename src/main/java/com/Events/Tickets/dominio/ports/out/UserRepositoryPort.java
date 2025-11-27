package com.Events.Tickets.dominio.ports.out;

import com.Events.Tickets.dominio.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByUsername(String username);
}
