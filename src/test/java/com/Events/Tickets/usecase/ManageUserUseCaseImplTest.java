package com.Events.Tickets.usecase;

import com.Events.Tickets.dominio.enums.Role;
import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.RegisterRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.AuthResponseDTO;
import com.Events.Tickets.infraestructura.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ManageUserUseCaseImpl manageUserUseCase;

    private RegisterRequestDTO registerRequest;
    private User savedUserMock;
    private final String rawPassword = "securepass";
    private final String encodedPassword = "encoded_securepass";

    @BeforeEach
    void setUp() {
        // 1. Instanciamos el DTO para la prueba
        registerRequest = new RegisterRequestDTO("testuser", "test@example.com", rawPassword, Role.USER);

        // 2. Mockeamos el objeto User que sería devuelto por la base de datos
        // Nota: Asegurarse que el constructor de User permite estos valores
        savedUserMock = new User(1L, "testuser", encodedPassword, Role.USER, null, null);
    }

    @Test
    void registerUser_shouldEncodePasswordAndSaveUserAndReturnAuthResponse() {
        String mockToken = "generated.jwt.token";

        // 1. Configurar Mocks
        // a) Simular el cifrado de la contraseña
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // b) Simular el guardado del usuario
        when(repositoryPort.save(any(User.class))).thenReturn(savedUserMock);

        // c) Simular la generación del Token. CORRECCIÓN: Usar any(UserDetails.class)
        // ya que el metodo generateToken requiere UserDetails, aunque le pases un User.
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(mockToken);

        // 2. Ejecutar (Pasa el DTO)
        AuthResponseDTO result = manageUserUseCase.register(registerRequest);

        // 3. Verificar
        assertNotNull(result);
        assertEquals(mockToken, result.token());

        // Verificamos que se llamó al cifrado
        verify(passwordEncoder, times(1)).encode(rawPassword);

        // Verificamos que se llamó a guardar con un objeto User que tiene la contraseña cifrada
        verify(repositoryPort, times(1)).save(argThat(user ->
                user.getPassword().equals(encodedPassword) &&
                        user.getUsername().equals(registerRequest.username())
        ));

        // Verificamos la generación del token (usando el tipo correcto para el mock)
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }
}