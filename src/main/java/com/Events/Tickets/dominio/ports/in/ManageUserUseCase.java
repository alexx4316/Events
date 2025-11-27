package com.Events.Tickets.dominio.ports.in;

import com.Events.Tickets.dominio.model.User;

public interface ManageUserUseCase {

    User registerUser(User user, String rawPassword);
    User findUserByUsername(String username);
}
