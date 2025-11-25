package com.Events.Tickets.infraestructura.adapters.out.jpa.repository;

import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<VenueEntity, Long> {
}
