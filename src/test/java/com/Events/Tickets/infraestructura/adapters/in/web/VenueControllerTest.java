package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.in.ManageVenueUseCase;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.VenueRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.VenueResponseDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.VenueWebMapper;
import com.Events.Tickets.infraestructura.security.CustomUserDetailsService;
import com.Events.Tickets.infraestructura.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = VenueController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurer.class
        ),
        excludeAutoConfiguration = {HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class}
)
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManageVenueUseCase manageVenueUseCase;

    @MockBean
    private VenueWebMapper mapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private final Venue baseModel = new Venue(1L, "Test Venue", "123 Street", "Medellín", "Colombia", 500, null, null);

    private VenueResponseDTO createBaseResponse(Long id, String name, String capacity) {
        return VenueResponseDTO.builder()
                .id(id)
                .name(name)
                .address("123 Street")
                .city("Medellín")
                .country("Colombia")
                .capacity(capacity)
                .build();
    }


    // -----------------------------------------
    //         1. CREATE VENUE (POST)
    // -----------------------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void createVenue_ShouldReturn201() throws Exception {

        VenueRequestDTO request = new VenueRequestDTO(
                "Test Venue", "123 Street", "Medellín", "Colombia", 500
        );

        VenueResponseDTO response = createBaseResponse(1L, "Test Venue", "500");

        Mockito.when(mapper.toModel(any(VenueRequestDTO.class))).thenReturn(baseModel);
        Mockito.when(manageVenueUseCase.create(any(Venue.class))).thenReturn(baseModel);
        Mockito.when(mapper.toResponseDto(any(Venue.class))).thenReturn(response);

        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    // -----------------------------------------
    //       2. GET BY ID (EXISTE)
    // -----------------------------------------
    @Test
    @WithMockUser
    void getVenue_ShouldReturn200_WhenFound() throws Exception {

        VenueResponseDTO response = createBaseResponse(1L, "Test Venue", "500");

        Mockito.when(manageVenueUseCase.findById(1L)).thenReturn(Optional.of(baseModel));
        Mockito.when(mapper.toResponseDto(baseModel)).thenReturn(response);

        mockMvc.perform(get("/venues/1").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // -----------------------------------------
    //     3. GET BY ID (NO EXISTE)
    // -----------------------------------------
    @Test
    @WithMockUser
    void getVenue_ShouldReturn404_WhenNotFound() throws Exception {

        Mockito.when(manageVenueUseCase.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/venues/99").with(anonymous()))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------
    //           4. LIST ALL VENUES
    // -----------------------------------------
    @Test
    @WithMockUser
    void getAllVenues_ShouldReturn200() throws Exception {

        VenueResponseDTO response = createBaseResponse(1L, "Test Venue", "500");

        Mockito.when(manageVenueUseCase.findAll()).thenReturn(List.of(baseModel));
        Mockito.when(mapper.toResponseDto(baseModel)).thenReturn(response);

        mockMvc.perform(get("/venues").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // -----------------------------------------
    //              5. UPDATE VENUE
    // -----------------------------------------
    @Test
    @WithMockUser(roles = "USER")
    void updateVenue_ShouldReturn200() throws Exception {

        VenueRequestDTO request = new VenueRequestDTO(
                "Updated Venue", "New Street", "Bogotá", "Colombia", 800
        );

        Venue updatedModel = new Venue(1L, "Updated Venue", "New Street", "Bogotá", "Colombia", 800, null, null);

        VenueResponseDTO response = VenueResponseDTO.builder()
                .id(1L)
                .name("Updated Venue")
                .address("New Street")
                .city("Bogotá")
                .country("Colombia")
                .capacity("800")
                .build();

        Mockito.when(mapper.toModel(any(VenueRequestDTO.class))).thenReturn(updatedModel);
        Mockito.when(manageVenueUseCase.update(eq(1L), any(Venue.class))).thenReturn(updatedModel);
        Mockito.when(mapper.toResponseDto(any(Venue.class))).thenReturn(response);

        mockMvc.perform(put("/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Venue"));
    }

    // -----------------------------------------
    //              6. DELETE VENUE
    // -----------------------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteVenue_ShouldReturn204() throws Exception {

        mockMvc.perform(delete("/venues/1").with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(manageVenueUseCase).delete(1L);
    }
}