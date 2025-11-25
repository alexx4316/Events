package com.Events.Tickets.dominio.ports.in;

import com.Events.Tickets.dominio.model.Venue;

import java.util.List;
import java.util.Optional;

public interface ManageVenueUseCase {
    Venue create(Venue venue);
    Optional<Venue> findById(Long id);
    List<Venue> findAll();
    Venue update(Long id, Venue venue);
    void delete(Long id);
}
