package com.Events.Tickets.usecase;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.out.VenueRepositoryPort;
import com.Events.Tickets.exception.ResourceNotFoundException;
import com.Events.Tickets.dominio.ports.in.ManageVenueUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageVenueUseCaseImpl implements ManageVenueUseCase {

    private final VenueRepositoryPort repositoryPort;

    @Override
    @Transactional
    public Venue create(Venue venue) {
        return repositoryPort.create(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venue> findById(Long id) {
        Optional<Venue> venue = repositoryPort.findById(id);
        if (venue.isEmpty()){
            throw new ResourceNotFoundException("Venue not found with ID " + id);
        }
        return venue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> findAll() {
        return repositoryPort.findAll();
    }

    @Override
    @Transactional
    public Venue update(Long id, Venue venue) {
        return repositoryPort.update(id,venue);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repositoryPort.delete(id);
    }
}