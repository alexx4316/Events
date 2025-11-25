package com.Events.Tickets.dominio.model;


import com.Events.Tickets.entity.EventType;
import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;
    private String description;
    private EventType eventType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer availableTickets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Venue venue;

    public Event() {
    }

    public Event(Long id, String name, String description, EventType eventType, LocalDateTime startDate, LocalDateTime endDate, Integer availableTickets, LocalDateTime createdAt, LocalDateTime updatedAt, Venue venue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableTickets = availableTickets;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.venue = venue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}

