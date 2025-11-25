package com.Events.Tickets.dominio.ports.out;

import com.Events.Tickets.dominio.model.Venue;

import java.util.List;
import java.util.Optional;

public interface VenueRepositoryPort {
    Venue create(Venue venue);
    Optional<Venue> findById(Long id);
    List<Venue> findAll();
    Venue update(Long id, Venue venue);
    void delete(Long id);
}
