package com.Events.Tickets.usecase;


import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.in.ManageEventUseCase;
import com.Events.Tickets.dominio.ports.in.ManageVenueUseCase;
import com.Events.Tickets.dominio.ports.out.EventRepositoryPort;
import com.Events.Tickets.entity.EventType;
import com.Events.Tickets.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageEventUseCaseImpl implements ManageEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;
    private final ManageVenueUseCase manageVenueUseCase;


    // ------------------ CREATE ------------------
    @Override
    @Transactional
    public Event create(Event event, Long venueId) {
        Venue venue = manageVenueUseCase.findById(venueId).orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + venueId));
        event.setVenue(venue);
        return eventRepositoryPort.create(event);
    }

    // ------------------ READ (by ID) ------------------
    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findById(Long id) {
        Optional<Event> event = eventRepositoryPort.findById(id);

        if(event.isEmpty()){
            throw new ResourceNotFoundException("Event not found with ID " + id);
        }
        return event;
    }

    // ------------------ READ (all) ------------------
    @Override
    @Transactional(readOnly = true)
    public List<Event> findAll() {
        return eventRepositoryPort.findAll();
    }

    // ------------------ UPDATE ------------------
    @Override
    @Transactional
    public Event update(Long id, Event event) {
        return eventRepositoryPort.update(id, event);
    }

    // ------------------ DELETE ------------------
    @Override
    @Transactional
    public void delete(long id) {
        eventRepositoryPort.delete(id);
    }

    // ---------------- FIND EVENTS ----------------
    @Override
    @Transactional(readOnly = true)
    public List<Event> findEvents(String city, EventType eventType, int page, int size){
        return eventRepositoryPort.findEvents(city,eventType,page,size);
    }
}
