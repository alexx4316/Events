package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.VenueRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@TestPropertySource(
        properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.datasource.driverClassName=com.Events.Tickets.Test",
                "spring.jpa.database-platform=com.Events.Tickets.Test",
                "spring.h2.console.enabled=false",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration"
        }
)
class VenueControllerIntegrationTest {

    // Contenedor real de PostgreSQL
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.2-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // Pasar datos del contenedor a Spring
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private VenueRequestDTO baseVenue;

    @BeforeEach
    void setup() {
        baseVenue = new VenueRequestDTO(
                "Test Venue",
                "Street 123",
                "Medellín",
                "Colombia",
                1000
        );
    }

    // ----------------------------------------------------------------------
    // 1. CREATE VENUE
    // ----------------------------------------------------------------------
    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void createVenue_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseVenue)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    // ----------------------------------------------------------------------
    // 2. GET VENUE BY ID
    // ----------------------------------------------------------------------
    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void getVenueById_ShouldReturn200() throws Exception {

        // Crear primero
        String json = mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseVenue)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(json).get("id").asLong();

        // Consultar
        mockMvc.perform(get("/venues/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    // ----------------------------------------------------------------------
    // 3. UPDATE VENUE
    // ----------------------------------------------------------------------
    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void updateVenue_ShouldReturn200() throws Exception {

        // Crear primero
        String json = mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseVenue)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(json).get("id").asLong();

        // Modificar
        VenueRequestDTO update = new VenueRequestDTO(
                "Updated Venue",
                "New Address",
                "Bogotá",
                "Colombia",
                2000
        );

        mockMvc.perform(put("/venues/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Venue"))
                .andExpect(jsonPath("$.city").value("Bogotá"));
    }

    // ----------------------------------------------------------------------
    // 4. DELETE VENUE
    // ----------------------------------------------------------------------
    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void deleteVenue_ShouldReturn204() throws Exception {

        // Crear primero
        String json = mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseVenue)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/venues/" + createdId))
                .andExpect(status().isNoContent());
    }
}
