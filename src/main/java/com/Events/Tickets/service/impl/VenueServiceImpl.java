package com.Events.Tickets.service.impl;

import com.Events.Tickets.dto.request.VenueRequestDTO;
import com.Events.Tickets.dto.response.VenueResponseDTO;
import com.Events.Tickets.entity.VenueEntity;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.mapper.VenueMapper;
import com.Events.Tickets.repository.VenueRepository;
import com.Events.Tickets.service.VenueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    // Inyección por Constructor
    public VenueServiceImpl(VenueRepository venueRepository, VenueMapper venueMapper) {
        this.venueRepository = venueRepository;
        this.venueMapper = venueMapper;
    }

    @Transactional
    @Override
    public VenueResponseDTO create(VenueRequestDTO dto) {
        // 1. Mapear DTO (Petición) a Entidad
        VenueEntity venueToSave = venueMapper.toEntity(dto);

        // 2. Guardar en la base de datos
        VenueEntity savedVenue = venueRepository.save(venueToSave);

        // 3. Mapear la Entidad guardada (con ID y fechas de auditoría) a DTO de Respuesta
        return venueMapper.toResponseDTO(savedVenue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponseDTO findById(Long id) {
        // Usa findById del Repositorio. Si no existe, lanza nuestra excepción 404 personalizada.
        VenueEntity venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recinto (Venue) no encontrado con ID: " + id));

        return venueMapper.toResponseDTO(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponseDTO> findAll() {
        // Llama a findAll del Repositorio, convierte la lista de Entidades a lista de DTOs.
        return venueRepository.findAll()
                .stream()
                .map(venueMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueResponseDTO update(Long id, VenueRequestDTO dto) {
        // 1. Verificar si existe (404)
        VenueEntity existingVenue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recinto (Venue) no encontrado con ID: " + id));

        // 2. Actualizar campos
        existingVenue.setName(dto.getName());
        existingVenue.setAddress(dto.getAddress());
        existingVenue.setCity(dto.getCity());
        existingVenue.setCountry(dto.getCountry());
        existingVenue.setCapacity(dto.getCapacity());

        // 3. Guardar (JPA detecta que es un UPDATE)
        VenueEntity updatedVenue = venueRepository.save(existingVenue);

        return venueMapper.toResponseDTO(updatedVenue);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 1. Verificar si existe (404)
        VenueEntity venueToDelete = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recinto (Venue) no encontrado con ID: " + id));

        // 2. Eliminar. Debido a la configuración de 'cascade' en EventEntity, los eventos asociados se borran.
        venueRepository.delete(venueToDelete);
    }
}