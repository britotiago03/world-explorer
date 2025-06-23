package com.worldexplorer.countryservice.controller;

import com.worldexplorer.countryservice.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.worldexplorer.countryservice.testdata.CountryTestDataFactory.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for POST operations on Country endpoints (creating countries).
 */
class CreateCountryControllerTest extends BaseCountryControllerTest {

    @Test
    void createCountry_WithValidData_ShouldReturn200AndCreatedCountry() throws Exception {
        // Given
        Country newCountry = createDenmark();
        Country savedCountry = createDenmarkWithId(3L);

        when(countryService.save(any(Country.class))).thenReturn(savedCountry);

        // When & Then
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCountry)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Denmark")))
                .andExpect(jsonPath("$.officialName", is("Kingdom of Denmark")))
                .andExpect(jsonPath("$.isoCode", is("DK")))
                .andExpect(jsonPath("$.capital", is("Copenhagen")))
                .andExpect(jsonPath("$.currency", is("DKK")));

        verify(countryService, times(1)).save(any(Country.class));
        verify(eventPublisherService, times(1)).publishCountryCreated(eq(savedCountry));
    }

    @Test
    void createCountry_WithMinimalData_ShouldReturn200() throws Exception {
        // Given
        Country minimalCountry = createMinimal("TestCountry", "Test Country Official");
        Country savedCountry = createMinimalWithId(10L, "TestCountry", "Test Country Official");

        when(countryService.save(any(Country.class))).thenReturn(savedCountry);

        // When & Then
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("TestCountry")))
                .andExpect(jsonPath("$.officialName", is("Test Country Official")))
                .andExpect(jsonPath("$.area").doesNotExist())
                .andExpect(jsonPath("$.population").doesNotExist());

        verify(countryService, times(1)).save(any(Country.class));
        verify(eventPublisherService, times(1)).publishCountryCreated(eq(savedCountry));
    }

    @Test
    void createCountry_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then - Use malformed JSON that Jackson cannot parse
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))  // Missing closing quote and brace
                .andExpect(status().isBadRequest());

        verify(countryService, never()).save(any(Country.class));
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void createCountry_WithEmptyBody_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).save(any(Country.class));
        verifyNoInteractions(eventPublisherService);
    }
}