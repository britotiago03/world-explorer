package com.worldexplorer.countryservice.controller;

import com.worldexplorer.countryservice.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Country Controller focusing on cross-cutting concerns
 * like content type handling, CORS, and end-to-end workflows.
 */
class CountryControllerIntegrationTest extends BaseCountryControllerTest {

    @Test
    void allEndpoints_ShouldAcceptAndReturnJson() throws Exception {
        // Test that all endpoints properly handle Content-Type and Accept headers
        when(countryService.findAll()).thenReturn(List.of());
        when(countryService.findById(1L)).thenReturn(Optional.of(norwayCountry));
        when(countryService.existsById(1L)).thenReturn(true);
        when(countryService.save(any(Country.class))).thenReturn(norwayCountry);

        // GET all
        mockMvc.perform(get("/api/countries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // GET by id
        mockMvc.perform(get("/api/countries/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // POST
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(norwayCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // PUT
        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(norwayCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Verify event publishing for write operations
        verify(eventPublisherService, times(1)).publishCountryCreated(any(Country.class));
        verify(eventPublisherService, times(1)).publishCountryUpdated(any(Country.class), any(Country.class));
    }

    @Test
    void corsHeaders_ShouldBeHandledProperly() throws Exception {
        // Given
        when(countryService.findAll()).thenReturn(List.of());

        // When & Then - Test actual CORS request with Origin header
        mockMvc.perform(get("/api/countries")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));

        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void endToEndWorkflow_CreateUpdateDelete_ShouldWorkCorrectly() throws Exception {
        // Given
        Country newCountry = denmarkCountry;
        newCountry.setId(null); // New country without ID

        Country savedCountry = denmarkCountry;
        savedCountry.setId(3L);

        Country updatedCountry = denmarkCountry;
        updatedCountry.setId(3L);
        updatedCountry.setPopulation(6000000L);

        // Mock service calls
        when(countryService.save(any(Country.class)))
                .thenReturn(savedCountry)
                .thenReturn(updatedCountry);
        when(countryService.existsById(3L)).thenReturn(true);
        when(countryService.findById(3L))
                .thenReturn(Optional.of(savedCountry))
                .thenReturn(Optional.of(updatedCountry));

        // Step 1: Create country
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCountry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Denmark"));

        // Step 2: Update country
        mockMvc.perform(put("/api/countries/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.population").value(6000000));

        // Step 3: Delete country
        mockMvc.perform(delete("/api/countries/3"))
                .andExpect(status().isNoContent());

        // Verify all events were published
        verify(eventPublisherService, times(1)).publishCountryCreated(eq(savedCountry));
        verify(eventPublisherService, times(1)).publishCountryUpdated(eq(updatedCountry), eq(savedCountry));
        verify(eventPublisherService, times(1)).publishCountryDeleted(eq(updatedCountry));
    }
}