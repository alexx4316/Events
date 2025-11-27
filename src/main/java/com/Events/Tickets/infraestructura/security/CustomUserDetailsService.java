package com.Events.Tickets.infraestructura.security;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.in.ManageUserUseCase;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // Inyectamos el Puerto de Entrada del Dominio
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Llamar directamente al puerto de salida (repositorio)
        User domainUser = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Usar el adaptador para devolver UserDetails
        return new CustomUserDetails(domainUser);
    }
}