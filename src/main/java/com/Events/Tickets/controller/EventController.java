package com.Events.Tickets.controller;

import com.Events.Tickets.dto.request.EventRequestDTO;
import com.Events.Tickets.dto.response.EventResponseDTO;
import com.Events.Tickets.entity.EventType;
import com.Events.Tickets.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Event Catalog Management")
public class EventController {

    private final EventService eventService;

    // Inyección de dependencias
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ------------------ POST /events (Crear Evento) ------------------
    @PostMapping
    @Operation(summary = "Create a new Event.",
            description = "It requires the ID of an existing Venue and applies validations such as future dates and required fields.")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO dto) {
        // @Valid: Activa las validaciones del DTO (NotBlank, Future, etc.)
        EventResponseDTO createdEvent = eventService.create(dto);
        // Retorna 201 Created
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    // ------------------ GET /events/{id} (Buscar por ID) ------------------
    @GetMapping("/{id}")
    @Operation(summary = "Search for an Event by its ID.")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.findById(id);
        // Retorna 200 OK. Si no existe, el Service lanza una 404 (manejada globalmente).
        return ResponseEntity.ok(event);
    }

    // ------------------ GET /events (Listar con Paginación y Filtros) ------------------
    @GetMapping
    @Operation(summary = "List of Events with Pagination and Filters",
            description = "It allows you to obtain a list of events with pagination., " +
                    "optionally filtering by city and event type.")
            // Añadimos esta anotación para que Swagger muestre los parámetros de Pageable correctamente:
            @Parameter(name = "pageable", hidden = true)
            public ResponseEntity<Page<EventResponseDTO>> getFilteredEvents(

            // Documentación del filtro 'city'
            @Parameter(name = "city", description = "Optional filter by city of the location (Venue).", in = ParameterIn.QUERY)
            @RequestParam(required = false) String city,

            // Documentación del filtro 'eventType'
            @Parameter(name = "eventType", description = "Optional filter by event type (CONCERT, CONFERENCE, OTHER, SPORTS, THEATER).", in = ParameterIn.QUERY)
            @RequestParam(required = false) EventType eventType,

            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {

        Page<EventResponseDTO> eventsPage = eventService.findEvents(city, eventType, pageable);
        return ResponseEntity.ok(eventsPage);
    }

    // ------------------ PUT /events/{id} (Actualizar Evento) ------------------
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Event by its ID.",
            description = "It applies field validations and name uniqueness..")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO dto) {

        EventResponseDTO updatedEvent = eventService.update(id, dto);
        // Retorna 200 OK.
        return ResponseEntity.ok(updatedEvent);
    }

    // ------------------ DELETE /events/{id} (Eliminar Evento) ------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an Event by its ID.")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
        // Retorna 204 No Content.
        return ResponseEntity.noContent().build();
    }
}