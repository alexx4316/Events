package com.Events.Tickets.controller;

import com.Events.Tickets.dto.request.VenueRequestDTO;
import com.Events.Tickets.dto.response.VenueResponseDTO;
import com.Events.Tickets.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
@Tag(name = "Venues", description = "Venue and Location Management for Events")
public class VenueController {

    private final VenueService venueService;

    // Inyección de Dependencias
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    // ------------------ 1. POST /venues (Crear) ------------------
    @PostMapping
    @Operation(summary = "Create a new venue.")
    public ResponseEntity<VenueResponseDTO> createVenue(@Valid @RequestBody VenueRequestDTO dto) {
        // @Valid activa las anotaciones de validación del DTO
        VenueResponseDTO createdVenue = venueService.create(dto);
        // Retorna 201 Created
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    // ------------------ 2. GET /venues/{id} (Buscar por ID) ------------------
    @GetMapping("/{id}")
    @Operation(summary = "Search for a venue by its ID.")
    public ResponseEntity<VenueResponseDTO> getVenueById(@PathVariable Long id) {
        VenueResponseDTO venue = venueService.findById(id);
        // Retorna 200 OK.
        return ResponseEntity.ok(venue);
    }

    // ------------------ 3. GET /venues (Listar Todos) ------------------
    @GetMapping
    @Operation(summary = "List all venues.")
    public ResponseEntity<List<VenueResponseDTO>> getAllVenues() {
        List<VenueResponseDTO> venues = venueService.findAll();
        return ResponseEntity.ok(venues);
    }

    // ------------------ 4. PUT /venues/{id} (Actualizar) ------------------
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing venue by its ID.")
    public ResponseEntity<VenueResponseDTO> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequestDTO dto) {

        VenueResponseDTO updatedVenue = venueService.update(id, dto);
        // Retorna 200 OK.
        return ResponseEntity.ok(updatedVenue);
    }

    // ------------------ 5. DELETE /venues/{id} (Eliminar) ------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a venue by its ID.")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.delete(id);
        // Retorna 204 No Content
        return ResponseEntity.noContent().build();
    }
}