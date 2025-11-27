// com.Events.Tickets.infraestructura.adapters.in.web.AuthController

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ManageUserUseCase manageUserUseCase;
    private final JwtService jwtService;

    // ----------------------------------------------------
    // ENDPOINT 1: REGISTRO (/auth/register)
    // ----------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid RegisterRequestDTO request) {

        // 1. Mapear DTO a Dominio (asumiendo el rol por defecto)
        User newUser = new User();

        // Establecemos solo los campos que vienen del DTO
        newUser.setUsername(request.username());
        newUser.setRole(Role.USER);

        // 2. Llamar al Caso de Uso (donde se cifra la contraseña)
        User registeredUser = manageUserUseCase.registerUser(newUser, request.password());

        // 3. Devolver la representación del usuario registrado
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // ----------------------------------------------------
    // ENDPOINT 2: LOGIN (/auth/login)
    // ----------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {

        // 1. Intenta autenticar. Si las credenciales son incorrectas, lanza una excepción.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // 2. Si es exitoso, genera el token usando los detalles del usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDTO(jwtToken));
    }
}