package com.Events.Tickets.repository;

import com.Events.Tickets.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<VenueEntity, Long> {
}
