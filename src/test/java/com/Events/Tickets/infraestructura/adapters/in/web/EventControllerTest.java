package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.enums.EventType;
import com.Events.Tickets.dominio.ports.in.ManageEventUseCase;
import com.Events.Tickets.dominio.ports.out.VenueRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.EventRequestDTO;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.UserRepository; // 🔑 IMPORT DE REPOSITORIO DE USUARIOS
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.DateTimeMapper;
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.EventWebMapper;
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.VenueWebMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
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
class EventControllerTest {

    // --- CONFIGURACIÓN DE TESTCONTAINERS ---
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.2-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
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

    @Autowired
    private VenueRepositoryPort venueRepositoryPort;

    @Autowired
    private javax.sql.DataSource dataSource;


    // --- MOCKING DE DEPENDENCIAS ---

    @MockBean
    private UserRepository userRepository;

    private Long exisitingVenueId;

    @BeforeEach
    void setUp() throws Exception{

        // LIMPIAR BASE DE DATOS
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE venues RESTART IDENTITY CASCADE");
        }

        Venue testVenue = new Venue(null,"Test Venue", "Test city","Test city" ,"Test address", 100, null, null);
        Venue savedVenue = venueRepositoryPort.create(testVenue);
        exisitingVenueId = savedVenue.getId();
    }

    @Test
    // Se requiere @WithMockUser ya que la configuración de seguridad está activa
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void createEvent_ShouldReturn201AndCreatedEvent() throws Exception {

        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);

        EventRequestDTO requestDto = new EventRequestDTO();
        requestDto.setName("Global Tech Summit");
        requestDto.setDescription("Tech conference");
        requestDto.setStartDate(futureDate);
        requestDto.setEndDate(futureDate.plusHours(8));
        requestDto.setEventType(EventType.CONFERENCE);
        requestDto.setVenueId(exisitingVenueId);

        // Act & Assert
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Global Tech Summit"))
                .andExpect(jsonPath("$.eventType").value("CONFERENCE"))
                .andExpect(jsonPath("$.venue.id").value(exisitingVenueId));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void createEvent_ShouldReturn404_WhenVenueNotFound() throws Exception {
        // Arrange
        Long invalidVenueId = 999L;

        EventRequestDTO requestDto = new EventRequestDTO();
        requestDto.setName("Event with no Venue");
        requestDto.setVenueId(invalidVenueId);

        // Act & Assert
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isBadRequest());
    }
}