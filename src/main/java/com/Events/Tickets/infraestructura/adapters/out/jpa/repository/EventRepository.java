package com.Events.Tickets.infraestructura.adapters.out.jpa.repository;

import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.EventEntity;
import com.Events.Tickets.dominio.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    Optional<EventEntity> findByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    // ------------------ OPTIMIZACIÓN N+1 Y FILTRADO AVANZADO ------------------

    // 1. Optimización: Usamos JOIN FETCH para cargar el 'venue' en la misma consulta.
    // Esto resuelve el N+1 para los listados.
    @Query("SELECT e FROM EventEntity e JOIN FETCH e.venue v WHERE " +
            "(:city IS NULL OR v.city = :city) AND " +
            "(:eventType IS NULL OR e.eventType = :eventType)")
    Page<EventEntity> findAllFilteredAndJoined( // Cambié el nombre para reflejar la optimización
                                                @Param("city") String city,
                                                @Param("eventType") EventType eventType,
                                                Pageable pageable);

    // 2. Solución N+1 para buscar por ID: Cargamos el Venue también.
    @Query("SELECT e FROM EventEntity e JOIN FETCH e.venue WHERE e.id = :id")
    Optional<EventEntity> findByIdWithVenue(@Param("id") Long id);

    // 3. Puedes agregar un metodo sin filtros si tu adapter lo necesita para listar todo:
    @Query("SELECT e FROM EventEntity e JOIN FETCH e.venue")
    Page<EventEntity> findAllWithVenue(Pageable pageable);
}
