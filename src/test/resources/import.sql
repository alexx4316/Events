TRUNCATE TABLE venues RESTART IDENTITY CASCADE;
INSERT INTO venues (id, name, address, city, country, capacity, created_at, updated_at) VALUES (1, 'Test Venue', 'Street 123', 'Medellín', 'Colombia', 1000, NOW(), NOW());
