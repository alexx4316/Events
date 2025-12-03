package com.Events.Tickets.usecase;

import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.in.ManageUserUseCase;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.LoginRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.RegisterRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.AuthResponseDTO;
import com.Events.Tickets.infraestructura.security.CustomUserDetails;
import com.Events.Tickets.infraestructura.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageUserUseCaseImpl implements ManageUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {

        userRepositoryPort.findByEmail(request.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already registered");
        });

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User saved = userRepositoryPort.save(user);

        // Adaptamos el usuario de dominio a UserDetails
        CustomUserDetails principal = new CustomUserDetails(saved);

        String token = jwtService.generateToken(principal);

        return new AuthResponseDTO(token);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        User user = userRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        CustomUserDetails principal = new CustomUserDetails(user);

        String token = jwtService.generateToken(principal);

        return new AuthResponseDTO(token);
    }

    @Override
    public User findByEmail(String email) {
       return userRepositoryPort.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found with email " + email));
    }

    @Override
    public User findById(Long id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepositoryPort.findAll();
    }
}
