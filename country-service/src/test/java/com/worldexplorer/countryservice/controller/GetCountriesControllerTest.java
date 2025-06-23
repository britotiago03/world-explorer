package com.worldexplorer.countryservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for GET operations on Country endpoints.
 */
class GetCountriesControllerTest extends BaseCountryControllerTest {

    @Test
    void getAllCountries_ShouldReturnListOfCountries() throws Exception {
        // Given
        List<com.worldexplorer.countryservice.model.Country> countryList = Arrays.asList(norwayCountry, swedenCountry);
        when(countryService.findAll()).thenReturn(countryList);

        // When & Then
        mockMvc.perform(get("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Norway")))
                .andExpect(jsonPath("$[0].officialName", is("Kingdom of Norway")))
                .andExpect(jsonPath("$[0].isoCode", is("NO")))
                .andExpect(jsonPath("$[0].capital", is("Oslo")))
                .andExpect(jsonPath("$[0].population", is(5421241)))
                .andExpect(jsonPath("$[0].currency", is("NOK")))
                .andExpect(jsonPath("$[0].officialLanguages", hasSize(2)))
                .andExpect(jsonPath("$[0].officialLanguages[0]", is("Norwegian")))
                .andExpect(jsonPath("$[0].officialLanguages[1]", is("Sami")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Sweden")))
                .andExpect(jsonPath("$[1].isoCode", is("SE")));

        verify(countryService, times(1)).findAll();
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void getAllCountries_WhenEmpty_ShouldReturnEmptyArray() throws Exception {
        // Given
        when(countryService.findAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(empty())));

        verify(countryService, times(1)).findAll();
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void getCountryById_WhenExists_ShouldReturnCountry() throws Exception {
        // Given
        when(countryService.findById(1L)).thenReturn(Optional.of(norwayCountry));

        // When & Then
        mockMvc.perform(get("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Norway")))
                .andExpect(jsonPath("$.officialName", is("Kingdom of Norway")))
                .andExpect(jsonPath("$.isoCode", is("NO")))
                .andExpect(jsonPath("$.capital", is("Oslo")))
                .andExpect(jsonPath("$.demonym", is("Norwegian")))
                .andExpect(jsonPath("$.area", is(385207.0)))
                .andExpect(jsonPath("$.waterPercent", is(6.0)))
                .andExpect(jsonPath("$.population", is(5421241)))
                .andExpect(jsonPath("$.populationDensity", is(14.0)))
                .andExpect(jsonPath("$.callingCode", is("+47")))
                .andExpect(jsonPath("$.internetTld", is(".no")))
                .andExpect(jsonPath("$.dateFormat", is("dd.mm.yyyy")))
                .andExpect(jsonPath("$.timezone", is("CET")))
                .andExpect(jsonPath("$.summerTimezone", is("CEST")))
                .andExpect(jsonPath("$.currency", is("NOK")))
                .andExpect(jsonPath("$.nativeNames", hasSize(2)))
                .andExpect(jsonPath("$.nativeNames[0]", is("Norge")))
                .andExpect(jsonPath("$.nativeNames[1]", is("Noreg")))
                .andExpect(jsonPath("$.officialLanguages", hasSize(2)))
                .andExpect(jsonPath("$.officialLanguages", containsInAnyOrder("Norwegian", "Sami")))
                .andExpect(jsonPath("$.recognizedLanguages", hasSize(3)))
                .andExpect(jsonPath("$.recognizedLanguages", containsInAnyOrder("Norwegian", "Sami", "Kven")))
                .andExpect(jsonPath("$.flagUrl", is("https://example.com/norway-flag.png")))
                .andExpect(jsonPath("$.coatOfArmsUrl", is("https://example.com/norway-coat.png")));

        verify(countryService, times(1)).findById(1L);
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void getCountryById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(countryService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/countries/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).findById(999L);
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void getCountryById_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/countries/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).findById(anyLong());
        verifyNoInteractions(eventPublisherService);
    }
}