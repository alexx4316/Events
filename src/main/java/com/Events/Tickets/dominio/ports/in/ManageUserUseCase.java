package com.Events.Tickets.dominio.ports.in;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.LoginRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.RegisterRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.AuthResponseDTO;

import java.util.List;

public interface ManageUserUseCase {

    // Registro de usuario
    AuthResponseDTO register(RegisterRequestDTO request);

    // Login de usuario
    AuthResponseDTO login(LoginRequestDTO request);

    // Consultar usuario actual por email (username)
    User findByEmail(String email);

    // Obtener usuario por ID
    User findById(Long id);

    // Listar todos los usuarios (solo ADMIN)
    List<User> findAll();
}
