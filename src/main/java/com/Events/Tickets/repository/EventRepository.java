package com.Events.Tickets.repository;

import com.Events.Tickets.entity.EventEntity;
import com.Events.Tickets.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    @Query("SELECT e FROM EventEntity e JOIN e.venue v WHERE " +
            "(:city IS NULL OR v.city = :city) AND " +
            "(:eventType IS NULL OR e.eventType = :eventType)")
    Page<EventEntity> findAllFiltered(
            @Param("city") String city,
            @Param("eventType") EventType eventType,
            Pageable pageable);
}
