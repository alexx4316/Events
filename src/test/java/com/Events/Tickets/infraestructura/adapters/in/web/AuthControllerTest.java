package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.ports.in.ManageUserUseCase;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.LoginRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.RegisterRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.AuthResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    // Dependencias que el controlador utiliza (se mockean)
    @Mock
    private ManageUserUseCase manageUserUseCase;

    @Mock
    private AuthenticationManager authenticationManager;


    // Clase a probar
    @InjectMocks
    private AuthController authController;

    // --- Prueba de Registro ---

    @Test
    void testRegisterUser_shouldReturnCreatedAndAuthResponse() {
        // 1. Datos de entrada
        RegisterRequestDTO request = new RegisterRequestDTO("newuser", "new@example.com", "securepass", null);

        // 2. Simulación de la respuesta del Caso de Uso
        AuthResponseDTO mockResponse = new AuthResponseDTO("jwt-token-register");
        when(manageUserUseCase.register(any(RegisterRequestDTO.class))).thenReturn(mockResponse);

        // 3. Ejecutar
        ResponseEntity<AuthResponseDTO> response = authController.registerUser(request);

        // 4. Verificar
        // Verificar que se llamó al caso de uso
        verify(manageUserUseCase, times(1)).register(request);

        // Verificar el estado HTTP y el contenido
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-register", response.getBody().token());
    }

    // --- Prueba de Login ---

    @Test
    void testLogin_shouldReturnOkAndAuthResponse() {
        // 1. Datos de entrada
        LoginRequestDTO request = new LoginRequestDTO("pollo@example.com", "1234");

        // 2. Simulación de la AUTENTICACIÓN
        // Creamos un mock de Authentication que el AuthenticationManager devolverá.
        Authentication mockAuthentication = mock(Authentication.class);

        // Configuramos el mock para simular que la autenticación es exitosa
        // (Resuelve el error "Wanted but not invoked")
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        // 3. Simulación de la RESPUESTA del Caso de Uso
        AuthResponseDTO mockResponse = new AuthResponseDTO("jwt-token-login");
        when(manageUserUseCase.login(any(LoginRequestDTO.class))).thenReturn(mockResponse);

        // 4. Ejecutar
        ResponseEntity<AuthResponseDTO> response = authController.login(request);

        // 5. Verificar

        // Verificar que la autenticación fue llamada
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verificar que se llamó al caso de uso de login
        verify(manageUserUseCase, times(1)).login(any(LoginRequestDTO.class));

        // Verificar el estado HTTP y el contenido
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-login", response.getBody().token());
    }
}