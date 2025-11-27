package com.Events.Tickets.usecase;

import com.Events.Tickets.dominio.enums.Role;
import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.in.ManageUserUseCase;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageUserUseCaseImpl implements ManageUserUseCase {

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user, String rawPassword) {

        // Ciframos la contraseña
        String encodedPassword = passwordEncoder.encode(rawPassword);

        user.setPassword(encodedPassword);
        return repositoryPort.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return repositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username" + username));
    }
}
