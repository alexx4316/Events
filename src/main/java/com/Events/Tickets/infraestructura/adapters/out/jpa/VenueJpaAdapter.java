package com.Events.Tickets.infraestructura.adapters.out.jpa;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.out.VenueRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.VenueEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.mappers.VenueJpaMapper;
import com.Events.Tickets.infraestructura.adapters.out.jpa.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VenueJpaAdapter implements VenueRepositoryPort {

    private final VenueRepository venueRepository;
    private final VenueJpaMapper mapper;

    @Override
    public Venue create(Venue venue) {

        // 1. Dominio -> Entidad (Mapeo de entrada)
        VenueEntity entity = mapper.toEntity(venue);

        // 2. Operación de Persistencia
        VenueEntity savedEntity = venueRepository.save(entity);

        // 3. Entidad -> Dominio (Mapeo de salida)
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Venue> findById(Long id) {

        // 1. Operación de Persistencia
        return venueRepository.findById(id)
                // 2. Mapeo: Si encuentra la entidad, la convierte a Dominio
                .map(mapper::toDomain);
    }

    @Override
    public List<Venue> findAll() {

        // 1. Operación de Persistencia (obtiene todas las entidades)
        List<VenueEntity> entities = venueRepository.findAll();

        // 2. Mapeo: Convierte cada Entidad a Modelo de Dominio y retorna la lista
        return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Venue update(Long id, Venue venue) {

        // 1. Verificar si existe (usando la excepción de negocio)
        VenueEntity existingEntity = venueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + id));

        // 2. Actualizar campos de la entidad existente con datos del modelo de dominio
        existingEntity.setName(venue.getName());
        existingEntity.setAddress(venue.getAddress());
        existingEntity.setCity(venue.getCity());
        existingEntity.setCountry(venue.getCountry());
        existingEntity.setCapacity(venue.getCapacity());

        // 3. Guardar y Mapear de vuelta a Dominio
        VenueEntity updatedEntity = venueRepository.save(existingEntity);
        return mapper.toDomain(updatedEntity);
    }

    @Override
    public void delete(Long id) {

        // La validación de existencia se puede mover aquí si el RepositoryPort lo requiere
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venue not found with ID: " + id);
        }
        venueRepository.deleteById(id);
    }
}
