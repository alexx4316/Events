package com.Events.Tickets.infraestructura.adapters.in.web;

import com.Events.Tickets.dominio.model.Venue;
import com.Events.Tickets.dominio.ports.in.ManageVenueUseCase;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.VenueRequestDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.dto.response.VenueResponseDTO;
import com.Events.Tickets.infraestructura.adapters.in.web.mappers.VenueWebMapper;
import com.Events.Tickets.infraestructura.security.CustomUserDetailsService;
import com.Events.Tickets.infraestructura.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VenueController.class)
@MockBean(JpaMetamodelMappingContext.class)
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManageVenueUseCase manageVenueUseCase;

    @MockBean
    private VenueWebMapper venueWebMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Venue venue;
    private VenueRequestDTO venueRequestDTO;
    private VenueResponseDTO venueResponseDTO;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setId(1L);
        venue.setName("Test Venue");
        venue.setAddress("123 Test St");
        venue.setCity("Test City");
        venue.setCountry("Test Country");
        venue.setCapacity(1000);

        venueRequestDTO = new VenueRequestDTO("Test Venue", "123 Test St", "Test City", "Test Country", 1000);
        venueResponseDTO = VenueResponseDTO.builder()
                .id(1L)
                .name("Test Venue")
                .address("123 Test St")
                .city("Test City")
                .country("Test Country")
                .capacity("1000")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVenue_ShouldReturn201() throws Exception {
        when(venueWebMapper.toModel(any(VenueRequestDTO.class))).thenReturn(venue);
        when(manageVenueUseCase.create(any(Venue.class))).thenReturn(venue);
        when(venueWebMapper.toResponseDto(any(Venue.class))).thenReturn(venueResponseDTO);

        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venueRequestDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    @Test
    @WithMockUser
    void getVenueById_ShouldReturn200() throws Exception {
        when(manageVenueUseCase.findById(anyLong())).thenReturn(Optional.of(venue));
        when(venueWebMapper.toResponseDto(any(Venue.class))).thenReturn(venueResponseDTO);

        mockMvc.perform(get("/venues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    @Test
    @WithMockUser
    void getVenueById_ShouldReturn404_WhenNotFound() throws Exception {
        when(manageVenueUseCase.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/venues/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllVenues_ShouldReturn200() throws Exception {
        when(manageVenueUseCase.findAll()).thenReturn(Collections.singletonList(venue));
        when(venueWebMapper.toResponseDto(any(Venue.class))).thenReturn(venueResponseDTO);

        mockMvc.perform(get("/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateVenue_ShouldReturn200() throws Exception {
        when(venueWebMapper.toModel(any(VenueRequestDTO.class))).thenReturn(venue);
        when(manageVenueUseCase.update(anyLong(), any(Venue.class))).thenReturn(venue);
        when(venueWebMapper.toResponseDto(any(Venue.class))).thenReturn(venueResponseDTO);

        mockMvc.perform(put("/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venueRequestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteVenue_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/venues/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
