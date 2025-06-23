package com.worldexplorer.countryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventPublisherService focusing on error handling and resilience.
 */
@ExtendWith(MockitoExtension.class)
class EventPublisherServiceErrorHandlingTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private EventPublisherService eventPublisherService;
    private Country testCountry;

    private static final String EVENT_SERVICE_URL = "http://test-event-service:3021";

    @BeforeEach
    void setUp() {
        eventPublisherService = new EventPublisherService(restTemplate, objectMapper, EVENT_SERVICE_URL);
        testCountry = CountryTestDataFactory.createNorwayWithId(1L);
    }

    @Test
    void publishCountryCreated_WhenJsonSerializationFails_ShouldNotThrowException() throws JsonProcessingException {
        // Given
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryCreated(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void publishCountryCreated_WhenRestCallFails_ShouldNotThrowException() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\"}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new RestClientException("Network error"));

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryCreated(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishCountryUpdated_WhenJsonSerializationFails_ShouldNotThrowException() throws JsonProcessingException {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryUpdated(testCountry, previousCountry));

        verify(objectMapper).writeValueAsString(any());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void publishCountryUpdated_WhenRestCallFails_ShouldNotThrowException() throws JsonProcessingException {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);
        String expectedJson = "{\"country\":\"countryJson\"}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new RestClientException("Network error"));

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryUpdated(testCountry, previousCountry));

        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishCountryDeleted_WhenJsonSerializationFails_ShouldNotThrowException() throws JsonProcessingException {
        // Given
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryDeleted(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void publishCountryDeleted_WhenRestCallFails_ShouldNotThrowException() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\"}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new RestClientException("Network error"));

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryDeleted(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishEvent_WithNonSuccessHttpStatus_ShouldLogWarningButNotThrow() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\"}";
        ResponseEntity<Object> errorResponse = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(errorResponse);

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryCreated(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishEvent_WithBadRequestStatus_ShouldNotThrow() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\"}";
        ResponseEntity<Object> badRequestResponse = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(badRequestResponse);

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryCreated(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishEvent_WithServiceUnavailableStatus_ShouldNotThrow() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\"}";
        ResponseEntity<Object> serviceUnavailableResponse = new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(serviceUnavailableResponse);

        // When & Then
        assertThatNoException().isThrownBy(() -> eventPublisherService.publishCountryCreated(testCountry));

        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }
}