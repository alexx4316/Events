package com.Events.Tickets.usecase;

import com.Events.Tickets.dominio.enums.EventType;
import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.in.ManageVenueUseCase;
import com.Events.Tickets.dominio.ports.out.EventRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageEventUseCaseImplTest {

    @Mock
    private EventRepositoryPort eventRepositoryPort;

    @Mock
    private ManageVenueUseCase manageVenueUseCase;

    @InjectMocks
    private ManageEventUseCaseImpl manageEventUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveEvent_WhenVenueExists() {
        // Arrange
        Long venueId = 1L;

        // 1. Instanciación del objeto Venue usando setters (sin Lombok Builder)
        Venue existingVenue = new Venue(); // Asume constructor vacío
        existingVenue.setId(venueId);
        existingVenue.setName("Venue Test");
        existingVenue.setAddress("123 Test St");
        existingVenue.setCity("City");
        existingVenue.setCountry("Country");
        existingVenue.setCapacity(500);

        // 2. Instanciación del objeto Evento usando setters
        Event eventToCreate = new Event(); // Asume constructor vacío
        eventToCreate.setName("New Concert");
        eventToCreate.setDescription("Description");
        eventToCreate.setStartDate(LocalDateTime.now().plusDays(1));
        eventToCreate.setEndDate(LocalDateTime.now().plusDays(2));
        eventToCreate.setEventType(EventType.CONCERT);

        // 3. Objeto que simula el retorno (debe tener ID y Venue asignado)
        Event savedEvent = new Event();
        savedEvent.setId(10L); // Simula el ID generado
        savedEvent.setName(eventToCreate.getName());
        savedEvent.setDescription(eventToCreate.getDescription());
        savedEvent.setStartDate(eventToCreate.getStartDate());
        savedEvent.setEndDate(eventToCreate.getEndDate());
        savedEvent.setEventType(eventToCreate.getEventType());
        savedEvent.setVenue(existingVenue); // Venue ya inyectado por el Use Case

        // Configuración de Mocks
        when(manageVenueUseCase.findById(venueId)).thenReturn(Optional.of(existingVenue));
        when(eventRepositoryPort.create(any(Event.class))).thenReturn(savedEvent);

        // Act
        Event resultEvent = manageEventUseCase.create(eventToCreate, venueId);

        // Assert
        assertNotNull(resultEvent);
        assertEquals(10L, resultEvent.getId());
        assertEquals(venueId, resultEvent.getVenue().getId());

        // Verificaciones
        verify(manageVenueUseCase, times(1)).findById(venueId);
        verify(eventRepositoryPort, times(1)).create(eventToCreate);
    }

    @Test
    void create_ShouldThrowException_WhenVenueDoesNotExist() {
        // Arrange
        Long nonExistentVenueId = 99L;
        Event eventToCreate = new Event(); // Asume constructor vacío
        eventToCreate.setName("Bad Event");
        eventToCreate.setDescription("Description");
        eventToCreate.setStartDate(LocalDateTime.now().plusDays(1));
        eventToCreate.setEventType(EventType.CONCERT);

        // Configuración del Mock: No encuentra el Venue
        when(manageVenueUseCase.findById(nonExistentVenueId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> manageEventUseCase.create(eventToCreate, nonExistentVenueId)
        );

        assertEquals("Venue not found with ID: " + nonExistentVenueId, exception.getMessage());

        // Verificaciones
        verify(manageVenueUseCase, times(1)).findById(nonExistentVenueId);
        verify(eventRepositoryPort, never()).create(any(Event.class));
    }
}