package com.Events.Tickets.infraestructura.adapters.out.jpa;

import com.Events.Tickets.AbstractIntegrationTest;
import com.Events.Tickets.dominio.enums.Role;
import com.Events.Tickets.dominio.model.User;
import com.Events.Tickets.dominio.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// SpringBootTest levanta el contexto completo, pero Testcontainers inyecta las propiedades de la DB.
@SpringBootTest
@ActiveProfiles("test-integration")
class UserJpaAdapterTest extends AbstractIntegrationTest {

    // Inyectamos el puerto de salida para probar la implementación real (el Adapter JPA)
    @Autowired
    private UserRepositoryPort userAdapter;


    @Test
    void saveAndFindUser_ShouldPersistCorrectly() {
        // 1. Arrange: Crear un objeto de Dominio (User)
        User newUser = new User();
        newUser.setUsername("jpatestuser");
        newUser.setPassword("secureHash");
        newUser.setRole(Role.USER);

        // 2. Act: Ejecutar la operación de persistencia
        User savedUser = userAdapter.save(newUser);

        // 3. Assert: Verificar la Persistencia
        assertNotNull(savedUser.getId(), "El ID debe ser generado por la DB, indicando persistencia.");
        assertEquals("jpatestuser", savedUser.getUsername());

        // 4. Act: Ejecutar la operación de búsqueda
        User foundUser = userAdapter.findByUsername("jpatestuser").orElse(null);

        // 5. Assert: Verificar la Búsqueda
        assertNotNull(foundUser, "El usuario debe ser encontrado en la DB.");
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(Role.USER, foundUser.getRole());
    }
}