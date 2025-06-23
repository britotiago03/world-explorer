package com.worldexplorer.countryservice.controller;

import com.worldexplorer.countryservice.model.Country;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.worldexplorer.countryservice.testdata.CountryTestDataFactory.createNorway;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for DELETE operations on Country endpoints.
 */
class DeleteCountryControllerTest extends BaseCountryControllerTest {

    @Test
    void deleteCountry_WhenExists_ShouldReturn204() throws Exception {
        // Given
        Country countryToDelete = createNorway();
        countryToDelete.setId(1L);

        when(countryService.existsById(1L)).thenReturn(true);
        when(countryService.findById(1L)).thenReturn(Optional.of(countryToDelete));

        // When & Then
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(1L);
        verify(countryService, times(1)).findById(1L);
        verify(countryService, times(1)).deleteById(1L);
        verify(eventPublisherService, times(1)).publishCountryDeleted(eq(countryToDelete));
    }

    @Test
    void deleteCountry_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(countryService.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/countries/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(999L);
        verify(countryService, never()).findById(anyLong());
        verify(countryService, never()).deleteById(anyLong());
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void deleteCountry_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/countries/invalid"))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).existsById(anyLong());
        verify(countryService, never()).findById(anyLong());
        verify(countryService, never()).deleteById(anyLong());
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void deleteCountry_WhenCountryNotFoundAfterExistsCheck_ShouldNotPublishEvent() throws Exception {
        // Given - simulate race condition where country exists check passes but findById returns empty
        when(countryService.existsById(1L)).thenReturn(true);
        when(countryService.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(1L);
        verify(countryService, times(1)).findById(1L);
        verify(countryService, times(1)).deleteById(1L);
        verifyNoInteractions(eventPublisherService);
    }
}