package com.Events.Tickets.service.impl;

import com.Events.Tickets.dto.request.EventRequestDTO;
import com.Events.Tickets.dto.response.EventResponseDTO;
import com.Events.Tickets.entity.EventEntity;
import com.Events.Tickets.entity.EventType;
import com.Events.Tickets.entity.VenueEntity;
import com.Events.Tickets.exception.DataConflictException;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.repository.EventRepository;
import com.Events.Tickets.repository.VenueRepository;
import com.Events.Tickets.service.EventService;
import com.Events.Tickets.mapper.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, VenueRepository venueRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.eventMapper = eventMapper;
    }

    // Metodo auxiliar para buscar el Venue
    private VenueEntity findVenueById(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue (Place) not found with ID: " + venueId));
    }

    // ------------------ CREATE ------------------
    @Override
    @Transactional
    public EventResponseDTO create(EventRequestDTO dto) {
        // 1. Validación de Duplicados (HU 2)
        if (eventRepository.findByName(dto.getName()).isPresent()) {
            throw new DataConflictException("The event with the name '" + dto.getName() + "' It already exists.");
        }

        // 2. Buscar y validar la existencia del Venue (Clave Foránea)
        VenueEntity venue = findVenueById(dto.getVenueId());

        // 3. Mapear DTO a Entidad
        EventEntity eventToSave = eventMapper.toEntity(dto);

        // 4. Establecer la relación de Venue
        eventToSave.setVenue(venue);

        // 5. Guardar
        EventEntity savedEvent = eventRepository.save(eventToSave);

        // 6. Mapear a DTO de Respuesta
        return eventMapper.toResponseDTO(savedEvent);
    }

    // ------------------ READ (by ID) ------------------
    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO findById(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        return eventMapper.toResponseDTO(event);
    }

    // ------------------ READ (all) ------------------
    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ------------------ UPDATE ------------------
    @Transactional
    @Override
    public EventResponseDTO update(Long id, EventRequestDTO dto) {
        // 1. Verificar si el evento existe (404)
        EventEntity existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        // 2. Validación de Duplicados (excluyendo el propio evento)
        if (eventRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new DataConflictException("The event with the name '" + dto.getName() + "' It already exists for another entry.");
        }

        // 3. Buscar y validar la existencia del nuevo Venue si se cambia
        VenueEntity newVenue = findVenueById(dto.getVenueId());

        // 4. Actualizar campos
        existingEvent.setName(dto.getName());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setEventType(dto.getEventType());
        existingEvent.setStartDate(dto.getStartDate());
        existingEvent.setEndDate(dto.getEndDate());
        existingEvent.setVenue(newVenue);

        // 5. Guardar (se actualizará el campo updatedAt)
        EventEntity updatedEvent = eventRepository.save(existingEvent);

        return eventMapper.toResponseDTO(updatedEvent);
    }

    // ------------------ DELETE ------------------
    @Override
    @Transactional
    public void delete(Long id) {
        EventEntity eventToDelete = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        eventRepository.delete(eventToDelete);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventResponseDTO> findEvents(String city, EventType eventType, Pageable pageable){
        Page<EventEntity> eventPage = eventRepository.findAllFiltered(city, eventType, pageable);
        return eventPage.map(eventMapper::toResponseDTO);
    }
}
