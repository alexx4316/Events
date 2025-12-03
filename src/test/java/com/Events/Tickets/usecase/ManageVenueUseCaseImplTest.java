package com.Events.Tickets.usecase;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.out.VenueRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageVenueUseCaseImplTest {

    @Mock
    private VenueRepositoryPort repositoryPort;

    @InjectMocks
    private ManageVenueUseCaseImpl manageVenueUseCase;

    private Venue venue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        venue = new Venue(
                1L,
                "Arena Medellín",
                "Av. Colombia 123",
                "Medellín",
                "Colombia",
                5000,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testCreateVenue() {
        when(repositoryPort.create(venue)).thenReturn(venue);

        Venue result = manageVenueUseCase.create(venue);

        assertNotNull(result);
        assertEquals("Arena Medellín", result.getName());
        verify(repositoryPort, times(1)).create(venue);
    }

    @Test
    void testFindById_WhenVenueExists() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(venue));

        Optional<Venue> result = manageVenueUseCase.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Arena Medellín", result.get().getName());
        verify(repositoryPort, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenVenueDoesNotExist() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> manageVenueUseCase.findById(1L));

        verify(repositoryPort, times(1)).findById(1L);
    }

    @Test
    void testFindAll() {
        Venue venue2 = new Venue(
                2L,
                "Teatro Bogotá",
                "Cll 45 #10-23",
                "Bogotá",
                "Colombia",
                1200,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repositoryPort.findAll()).thenReturn(Arrays.asList(venue, venue2));

        List<Venue> result = manageVenueUseCase.findAll();

        assertEquals(2, result.size());
        verify(repositoryPort, times(1)).findAll();
    }

    @Test
    void testUpdateVenue() {
        Venue updatedVenue = new Venue(
                1L,
                "Arena Actualizada",
                "Av. Colombia 123",
                "Medellín",
                "Colombia",
                6000,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repositoryPort.update(1L, updatedVenue)).thenReturn(updatedVenue);

        Venue result = manageVenueUseCase.update(1L, updatedVenue);

        assertEquals("Arena Actualizada", result.getName());
        verify(repositoryPort, times(1)).update(1L, updatedVenue);
    }

    @Test
    void testDeleteVenue() {
        doNothing().when(repositoryPort).delete(1L);

        manageVenueUseCase.delete(1L);

        verify(repositoryPort, times(1)).delete(1L);
    }
}
