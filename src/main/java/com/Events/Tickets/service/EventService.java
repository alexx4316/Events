package com.Events.Tickets.service;

import com.Events.Tickets.dto.request.EventRequestDTO;
import com.Events.Tickets.dto.response.EventResponseDTO;
import com.Events.Tickets.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventService {
    // CRUD Básico
    EventResponseDTO create(EventRequestDTO dto);

    EventResponseDTO findById(Long id);

    EventResponseDTO update(Long id, EventRequestDTO dto);

    void delete(Long id);

    List<EventResponseDTO> findAll();

    Page<EventResponseDTO> findEvents(String city, EventType eventType, Pageable pageable);

}
