package com.Events.Tickets.infraestructura.adapters.out.jpa;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.out.EventRepositoryPort;
import com.Events.Tickets.dominio.ports.out.VenueRepositoryPort;
import com.Events.Tickets.dominio.enums.EventType;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.EventEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.VenueEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.mappers.EventJpaMapper;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.EventRepository;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.VenueRepository;
import com.Events.Tickets.infraestructura.adapters.out.jpa.specification.EventSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventJpaAdapter implements EventRepositoryPort {

    private final EventRepository eventRepository;
    private final EventJpaMapper eventMapper;
    private final VenueRepositoryPort venueRepositoryPort;
    private final VenueRepository venueRepository;

    @Override
    public Event create(Event event) {

        // 1. Obtener el ID del Venue del objeto de Dominio
        Long venueId = event.getVenue().getId();

        // 2. Mapeamos el objeto de Dominio a la Entidad
        EventEntity entityToSave = eventMapper.toEntity(event);

        // 3. Obtenemos una REFERENCIA/PROXY del VenueEntity usando el ID.
        VenueEntity managedVenueReference = venueRepository.getReferenceById(venueId);

        // 4. Asignamos la referencia gestionada a la Entidad
        entityToSave.setVenue(managedVenueReference);

        // 5. Realizamos la consulta
        EventEntity savedEntity = eventRepository.save(entityToSave);

        // 6. Traducimos de entidad a dominio
        return eventMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findByIdWithVenue(id).map(eventMapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        Pageable pageable = PageRequest.of(0, 50);
        return eventRepository.findAllWithVenue(pageable).getContent().stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
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
    public List<Event> findEvents(String city, EventType eventType, LocalDateTime startDate, int page, int size) {

        // 1. Crear una lista de Specifications.
        List<Specification<EventEntity>> specs = new ArrayList<>();

        // Agregar especificaciones. Si alguna devuelve NULL, será ignorada por allOf.
        specs.add(EventSpecifications.hasCity(city));
        specs.add(EventSpecifications.hasType(eventType));
        specs.add(EventSpecifications.startDateAfter(startDate));

        // Unir todas las especificaciones con un AND lógico.
        Specification<EventEntity> spec = Specification.allOf(specs);

        // 2. Agregar la optimización FETCH al final (siempre)
        // Esto asegura que la consulta cargue el Venue en una sola consulta.
        spec = spec.and(EventSpecifications.fetchVenue());

        // 3. Crear el Pageable
        Pageable pageable = PageRequest.of(page, size);

        // 4. Ejecutar la consulta con Specifications y Pageable
        Page<EventEntity> result = eventRepository.findAll(spec, pageable);

        // 5. Mapear y retornar
        return result.getContent().stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }
}
