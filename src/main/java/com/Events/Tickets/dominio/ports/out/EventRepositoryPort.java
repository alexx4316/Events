package com.Events.Tickets.dominio.ports.out;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.entity.EventType;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {

    Event create(Event event);
    Optional<Event> findById(Long id);
    Event update(Long id, Event event);
    void delete(Long id);
    List<Event> findAll();

    List<Event> findEvents(String city, EventType eventType, int page, int size);
}
