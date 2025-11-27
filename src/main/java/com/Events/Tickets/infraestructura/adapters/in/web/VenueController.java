package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.VenueRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.VenueResponseDTO;
import com.Events.Tickets.dominio.ports.in.ManageVenueUseCase;
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.VenueWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/venues")
@Tag(name = "Venues", description = "Venue and Location Management for Events")
@RequiredArgsConstructor
public class VenueController {

    private final ManageVenueUseCase manageVenueUseCase;
    private final VenueWebMapper webMapper;

    // ------------------ 1. POST /venues (Crear) ------------------
    @PostMapping
    @Operation(summary = "Create a new venue.")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<VenueResponseDTO> createVenue(@Valid @RequestBody VenueRequestDTO dto) {

        // 1. traducimos el dto de entrada a modelo de dominio
        Venue venueToCreate = webMapper.toModel(dto);

        // 2. llamar al caso de uso
        Venue createdVenue = manageVenueUseCase.create(venueToCreate);

        // 3. Traducimos el modelo a dto
        VenueResponseDTO responseDTO = webMapper.toResponseDto(createdVenue);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // ------------------ 2. GET /venues/{id} (Buscar por ID) ------------------
    @GetMapping("/{id}")
    @Operation(summary = "Search for a venue by its ID.")
    public ResponseEntity<VenueResponseDTO> getVenueById(@PathVariable Long id) {

        // Llamamos al caso de uso
        Venue venue = manageVenueUseCase.findById(id).orElseThrow(()-> new RuntimeException("Venue not found"));

        // Traduciomos el modelo de dominio a dto de respuesta
        VenueResponseDTO responseDTO = webMapper.toResponseDto(venue);

        return ResponseEntity.ok(responseDTO);
    }

    // ------------------ 3. GET /venues (Listar Todos) ------------------
    @GetMapping
    @Operation(summary = "List all venues.")
    public ResponseEntity<List<VenueResponseDTO>> getAllVenues() {

        // Obtenemos las lista de modelos
        List<Venue> venues = manageVenueUseCase.findAll();

        // Mapeamos la lista de modelos
        List<VenueResponseDTO> responseDTOS = venues.stream().map(webMapper::toResponseDto).collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOS);
    }

    // ------------------ 4. PUT /venues/{id} (Actualizar) ------------------
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing venue by its ID.")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<VenueResponseDTO> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueRequestDTO dto) {

        // Traducimos el dto a modelo
        Venue venueToUpdate = webMapper.toModel(dto);

        // Llamamos al caso de uso
        Venue updatedVenu = manageVenueUseCase.update(id, venueToUpdate);

        // Traducimos el modelo a dto de respuesta
        VenueResponseDTO responseDTO = webMapper.toResponseDto(updatedVenu);

        return ResponseEntity.ok(responseDTO);
    }

    // ------------------ 5. DELETE /venues/{id} (Eliminar) ------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a venue by its ID.")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {

        // Llamamos al caso de uso
        manageVenueUseCase.delete(id);

        return ResponseEntity.noContent().build();
    }
}