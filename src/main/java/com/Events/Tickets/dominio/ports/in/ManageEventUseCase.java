package com.Events.Tickets.dominio.ports.in;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.entity.EventType;

import java.util.List;
import java.util.Optional;

public interface ManageEventUseCase {
    Event create(Event event, Long venueId);
    Optional<Event> findById(Long id);
    Event update(Long id, Event event);
    void delete(long id);
    List<Event> findAll();
    List<Event> findEvents(String city, EventType eventType, int page, int size);
}
