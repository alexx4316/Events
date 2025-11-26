package com.Events.Tickets.infraestructura.adapters.out.jpa.specification;

import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.EventEntity;
import com.Events.Tickets.infraestructura.adapters.out.jpa.entity.VenueEntity;
import com.Events.Tickets.entity.EventType;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public final class EventSpecifications {

    // 1. Filtro por Ciudad del Venue
    public static Specification<EventEntity> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return null;
            }
            // Navegamos a la relación 'venue' para acceder a su campo 'city'
            return criteriaBuilder.equal(
                    root.get("venue").get("city"), city);
        };
    }

    // 2. Filtro por Tipo de Evento
    public static Specification<EventEntity> hasType(EventType eventType) {
        return (root, query, criteriaBuilder) -> {
            if (eventType == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("eventType"), eventType);
        };
    }

    // 3. Filtro por Fecha de Inicio Mínima (Buscar eventos que empiezan DESPUÉS de una fecha)
    public static Specification<EventEntity> startDateAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), date);
        };
    }

    // 4. Filtro por Estado (Ejemplo: Activo/Cancelado - Asume que tienes un campo 'isCancelled' boolean)
    // Para el ejemplo, usaremos un campo hipotético:
    // public static Specification<EventEntity> isStatus(boolean active) {
    //     return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), active);
    // }

    // 5. Detectar y reducir el N+1 para Specifications
    // Al usar Specifications, el JOIN FETCH requiere un pequeño truco en el query.
    // Para asegurarnos de que la relación 'venue' se cargue, usamos un FETCH:
    public static Specification<EventEntity> fetchVenue() {
        return (root, query, criteriaBuilder) -> {
            // Este truco asegura que el FETCH se ejecute solo en consultas que lo permitan (no count)
            if (root.getJavaType() != EventEntity.class) {
                return null;
            }
            // Se realiza el JOIN FETCH en el Root.
            query.distinct(true).getRoots().forEach(r -> r.fetch("venue", jakarta.persistence.criteria.JoinType.INNER));
            return criteriaBuilder.conjunction();
        };
    }

    // Constructor privado para evitar instanciación
    private EventSpecifications() {}
}