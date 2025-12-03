package com.Events.Tickets.infraestructura.adapters.in.web.dto.request;

import com.Events.Tickets.dominio.enums.Role;

public record RegisterRequestDTO (String username, String email, String password, Role role){}
