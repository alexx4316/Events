package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.LoginRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// IMPORTANTE: Necesitas el enum Role para crear el Record si no está importado globalmente
import com.Events.Tickets.dominio.enums.Role;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // --- INTEGRACIÓN DE REGISTRO CORREGIDA ---
    @Test
    void testRegisterIntegration() throws Exception {
        // 1. CREACIÓN CORRECTA DEL RECORD (usando constructor y todos los campos)
        RegisterRequestDTO req = new RegisterRequestDTO("testUser", "test@example.com", "1234", Role.USER);

        // El endpoint ahora devuelve un AuthResponseDTO (con el token)
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // El AuthController devuelve HttpStatus.CREATED
                .andExpect(jsonPath("$.token").exists());
    }

    // --- INTEGRACIÓN DE LOGIN CORREGIDA ---
    @Test
    void testLoginIntegration() throws Exception {
        String testEmail = "tester2@example.com";
        String testPassword = "1234";

        // 1. Pre-registramos al usuario
        // CREACIÓN CORRECTA DEL RECORD DE REGISTRO
        RegisterRequestDTO register = new RegisterRequestDTO("tester2", testEmail, testPassword, Role.USER);

        // La prueba de integración asume que el endpoint es /auth/register, no /api/auth/register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 2. Login real
        // CREACIÓN CORRECTA DEL RECORD DE LOGIN (solo email y password)
        LoginRequestDTO login = new LoginRequestDTO(testEmail, testPassword);

        // La prueba de integración asume que el endpoint es /auth/login, no /api/auth/login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}