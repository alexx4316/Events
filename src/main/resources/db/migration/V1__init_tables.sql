-- V1__init_tables.sql

-- 1. Tabla de Venues
CREATE TABLE venues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6),
    address VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    city VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL
);

-- 2. Tabla de Eventos
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6),
    description VARCHAR(500) NOT NULL,
    end_date TIMESTAMP(6),
    event_type VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    start_date TIMESTAMP(6) NOT NULL,
    venue_id BIGINT NOT NULL
);