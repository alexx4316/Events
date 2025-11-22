package com.Events.Tickets.service;

import com.Events.Tickets.dto.request.VenueRequestDTO;
import com.Events.Tickets.dto.response.VenueResponseDTO;

import java.util.List;

public interface VenueService {
    VenueResponseDTO create(VenueRequestDTO dto);
    VenueResponseDTO findById(Long id);
    List<VenueResponseDTO> findAll();
    VenueResponseDTO update(Long id, VenueRequestDTO dto);
    void delete(Long id);
}
