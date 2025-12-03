package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.VenueRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration")
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Sql("/import.sql")
class VenueControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.2-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private VenueRequestDTO createVenueRequestDTO(String name, String address, String city, String country, int capacity) {
        return new VenueRequestDTO(name, address, city, country, capacity);
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    @DirtiesContext
    void createVenue_ShouldReturn201() throws Exception {
        VenueRequestDTO newVenue = createVenueRequestDTO("New Venue", "Street 456", "Bogotá", "Colombia", 2000);

        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVenue)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("New Venue"))
                .andExpect(jsonPath("$.city").value("Bogotá"));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void getVenueById_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/venues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Venue"))
                .andExpect(jsonPath("$.city").value("Medellín"));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    @DirtiesContext
    void updateVenue_ShouldReturn200() throws Exception {
        VenueRequestDTO updatedVenue = createVenueRequestDTO("Updated Venue", "New Address", "Cali", "Colombia", 1500);

        mockMvc.perform(put("/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVenue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Venue"))
                .andExpect(jsonPath("$.city").value("Cali"));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    @DirtiesContext
    void deleteVenue_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/venues/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/venues/1"))
                .andExpect(status().isNotFound());
    }
}
