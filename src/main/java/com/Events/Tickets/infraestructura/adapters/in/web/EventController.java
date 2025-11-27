package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.model.Event;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.EventRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.EventResponseDTO;
import com.Events.Tickets.dominio.enums.EventType;
import com.Events.Tickets.dominio.ports.in.ManageEventUseCase;
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.EventWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Event Catalog Management")
@RequiredArgsConstructor
public class EventController {

    private final ManageEventUseCase manageEventUseCase;
    private final EventWebMapper webMapper;

    // ------------------ POST /events (Crear Evento) ------------------
    @PostMapping
    @Operation(summary = "Create a new Event.",
            description = "It requires the ID of an existing Venue and applies validations such as future dates and required fields.")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO dto) {

        // Convertimos el dto a modelo de dominio
        Event eventToCreate = webMapper.toModel(dto);

        Long venueId = dto.getVenueId();

        // Ejecutamos el caso de uso
        Event createdEvent = manageEventUseCase.create(eventToCreate, venueId);

        // Convertimos el diminio a dto
        EventResponseDTO responseDTO = webMapper.toResponseDto(createdEvent);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // ------------------ GET /events/{id} (Buscar por ID) ------------------
    @GetMapping("/{id}")
    @Operation(summary = "Search for an Event by its ID.")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {

        // Ejecutamos el caso de uso
        Optional<Event> evenOptional = manageEventUseCase.findById(id);

        // Mensaje de validacion
        Event event = evenOptional.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id));

        // Convertimos de modelo de domini a respuetsa dto
        return ResponseEntity.ok(webMapper.toResponseDto(event));
    }

    // ------------------ GET /events (Listar con Paginación y Filtros) ------------------
    @GetMapping
    @Operation(summary = "List of Events with Pagination and Filters", description = "It allows you to obtain a list of events with pagination., " + "optionally filtering by city and event type.") @Parameter(name = "pageable", hidden = true)
    public ResponseEntity<List<EventResponseDTO>> findEventsFiltered(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Conversion de tipo de dato de la fecha
        LocalDateTime localDateTime = null;
        if (startDate != null) {
            // El Instant se convierte a la zona de sistema y luego a LocalDateTime.
            localDateTime = startDate.atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        // 1. Ejecutar Caso de Uso
        List<Event> eventList = manageEventUseCase.findEvents(city, eventType, localDateTime, page, size);

        // 2. List<Domain> -> List<DTO>
        return ResponseEntity.ok(eventList.stream()
                .map(webMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    // ------------------ PUT /events/{id} (Actualizar Evento) ------------------
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Event by its ID.",
            description = "It applies field validations and name uniqueness..")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDTO dto) {

        // Convertimos de dto a modelo
        Event eventToUpdate = webMapper.toModel(dto);

        // Ejecutamos el caso de uso
        Event updateEvent = manageEventUseCase.update(id, eventToUpdate);

        // Convertimos de modelo a dto
        return ResponseEntity.ok(webMapper.toResponseDto(updateEvent));
    }

    // ------------------ DELETE /events/{id} (Eliminar Evento) ------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an Event by its ID.")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<EventResponseDTO>> deleteEvent(@PathVariable Long id) {

        // Ejecutamos el caso de uso
        List<Event> events = manageEventUseCase.findAll();

        // Convertimos de modelo a dto
        List<EventResponseDTO> responseDTOS = events.stream().map(webMapper::toResponseDto).collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOS);
    }
}