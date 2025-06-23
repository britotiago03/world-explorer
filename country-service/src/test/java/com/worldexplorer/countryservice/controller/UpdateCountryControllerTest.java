package com.worldexplorer.countryservice.controller;

import com.worldexplorer.countryservice.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static com.worldexplorer.countryservice.testdata.CountryTestDataFactory.createNorway;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for PUT operations on Country endpoints (updating countries).
 */
class UpdateCountryControllerTest extends BaseCountryControllerTest {

    @Test
    void updateCountry_WhenExists_ShouldReturn200AndUpdatedCountry() throws Exception {
        // Given
        Country existingCountry = createNorway();
        existingCountry.setId(1L);
        existingCountry.setPopulation(5421241L);

        Country updatedCountry = createNorway();
        updatedCountry.setId(1L);
        updatedCountry.setPopulation(5500000L);
        updatedCountry.setPopulationDensity(14.3);

        when(countryService.existsById(1L)).thenReturn(true);
        when(countryService.findById(1L)).thenReturn(Optional.of(existingCountry));
        when(countryService.save(any(Country.class))).thenReturn(updatedCountry);

        // When & Then
        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Norway")))
                .andExpect(jsonPath("$.population", is(5500000)))
                .andExpect(jsonPath("$.populationDensity", is(14.3)));

        verify(countryService, times(1)).existsById(1L);
        verify(countryService, times(1)).findById(1L);
        verify(countryService, times(1)).save(any(Country.class));
        verify(eventPublisherService, times(1)).publishCountryUpdated(eq(updatedCountry), eq(existingCountry));
    }

    @Test
    void updateCountry_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        Country updatedCountry = createNorway();
        when(countryService.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/countries/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(999L);
        verify(countryService, never()).findById(anyLong());
        verify(countryService, never()).save(any(Country.class));
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void updateCountry_WithInvalidId_ShouldReturn400() throws Exception {
        // Given
        Country updatedCountry = createNorway();

        // When & Then
        mockMvc.perform(put("/api/countries/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).existsById(anyLong());
        verify(countryService, never()).findById(anyLong());
        verify(countryService, never()).save(any(Country.class));
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void updateCountry_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then - Use malformed JSON that Jackson cannot parse
        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))  // Missing closing quote and brace
                .andExpect(status().isBadRequest());

        verify(countryService, never()).existsById(anyLong());
        verify(countryService, never()).findById(anyLong());
        verify(countryService, never()).save(any(Country.class));
        verifyNoInteractions(eventPublisherService);
    }
}