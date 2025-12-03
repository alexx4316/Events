package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.enums.Role;
import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.in.ManageUserUseCase;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.LoginRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.RegisterRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.AuthResponseDTO;
import com.Events.Tickets.infraestructura.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ManageUserUseCase manageUserUseCase;
    private final JwtService jwtService; // Se mantiene por si se usa en otros flujos

    // ----------------------------------------------------
    // ENDPOINT 1: REGISTRO (/auth/register)
    // ----------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@RequestBody @Valid RegisterRequestDTO request) {

        // El Caso de Uso maneja la lógica de negocio (validación, cifrado, mapeo, token).
        AuthResponseDTO response = manageUserUseCase.register(request);

        // Devuelve el token con estado 201 CREATED.
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ----------------------------------------------------
    // ENDPOINT 2: LOGIN (/auth/login)
    // ----------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {

        // 1. Intenta autenticar. Si las credenciales son incorrectas, lanza una excepción
        // y el controlador retorna un error (manejo automático de Spring Security).
        // Usamos request.email() como el 'username' para el token.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. Si la autenticación es exitosa, delega al Caso de Uso para generar el token
        // y cualquier otra lógica de login.
        AuthResponseDTO response = manageUserUseCase.login(request);

        // 3. Devuelve la respuesta con el token.
        return ResponseEntity.ok(response);
    }
}