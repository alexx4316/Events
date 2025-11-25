package com.Events.Tickets.infraestructura.adapters.out.jpa;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.out.EventRepositoryPort;
import com.Events.Tickets.dominio.ports.out.VenueRepositoryPort;
import com.Events.Tickets.entity.EventType;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.EventEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.mappers.EventJpaMapper;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventJpaAdapter implements EventRepositoryPort {

    private final EventRepository eventRepository;
    private final EventJpaMapper eventMapper;
    private final VenueRepositoryPort venueRepositoryPort;

    @Override
    public Event create(Event event) {

        // Validamos el id de el venue
        Long venueId = event.getVenue().getId();

        // Buscamos el venue con ese id
        Venue existingVenue = venueRepositoryPort.findById(venueId).orElseThrow(()-> new RuntimeException("Location (Venue) not found" + venueId));

        // Validamos ya exista el venue
        event.setVenue(existingVenue);

        // Traduciomos de dominio a entidad
        EventEntity entityToSave = eventMapper.toEntity(event);

        // Realizamos la consulta
        EventEntity savedEntity = eventRepository.save(entityToSave);

        // Traducimos de entidad a dominio
        return eventMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id).map(eventMapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll().stream().map(eventMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Event update(Long id, Event event) {

        // Verificamos existencia del evento
        EventEntity existingEntity = eventRepository.findById(id).orElseThrow(()-> new RuntimeException("Event not found with ID " + id));

        // Relacion del venue
        Long venueId = event.getVenue().getId();
        Venue exisitingVenue = venueRepositoryPort.findById(venueId).orElseThrow(()-> new ResourceNotFoundException("Venue not found with ID " + venueId));

        // Actualizamos lo campos
        existingEntity.setName(event.getName());
        existingEntity.setDescription(event.getDescription());
        existingEntity.setEventType(event.getEventType());
        existingEntity.setStartDate(event.getStartDate());
        existingEntity.setEndDate(event.getEndDate());

        // Guardamos el nuevo evento
        EventEntity updatedEntity = eventRepository.save(existingEntity);

        // Mapeamos y lo traemos de vuelta
        return eventMapper.toDomain(updatedEntity);
    }

    @Override
    public void delete(Long id) {
        if (!eventRepository.existsById(id)){
            throw new ResourceNotFoundException("Event not found with ID" + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> findEvents(String city, EventType eventType, int page, int size) {

        // Traduccion de paginacion
        PageRequest pagerequest = PageRequest.of(page,size);

        // Ejecutamos la consulta
        Page<EventEntity> eventPage = eventRepository.findAllFiltered(city,eventType, pagerequest);

        // Hacemos la conversion de entidad a modelo de dominio y retornamos
        return eventPage.getContent().stream().map(eventMapper::toDomain).collect(Collectors.toList());
    }
}
