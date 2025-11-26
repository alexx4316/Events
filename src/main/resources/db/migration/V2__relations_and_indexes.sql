-- V2__relations_and_indexes.sql

-- 1. Restricciones de unicidad (Constraints)
ALTER TABLE events
ADD CONSTRAINT UK_event_name UNIQUE (name);

ALTER TABLE venues
ADD CONSTRAINT UK_venue_name_city UNIQUE (name, city);

-- 2. Llave foránea (Foreign Key)
ALTER TABLE events
ADD CONSTRAINT FK_event_venue
FOREIGN KEY (venue_id)
REFERENCES venues(id);