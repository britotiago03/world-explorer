package com.worldexplorer.countryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

/**
 * Unit tests for EventPublisherService focusing on input validation and null handling.
 */
@ExtendWith(MockitoExtension.class)
class EventPublisherServiceValidationTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private EventPublisherService eventPublisherService;

    private static final String EVENT_SERVICE_URL = "http://test-event-service:3021";

    @BeforeEach
    void setUp() {
        eventPublisherService = new EventPublisherService(restTemplate, objectMapper, EVENT_SERVICE_URL);
    }

    @Test
    void publishCountryCreated_WithNullCountry_ShouldNotPublishEvent() {
        // When
        eventPublisherService.publishCountryCreated(null);

        // Then
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void publishCountryCreated_WithNullCountryId_ShouldNotPublishEvent() {
        // Given
        Country testCountry = CountryTestDataFactory.createNorway();
        testCountry.setId(null);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void publishCountryUpdated_WithNullUpdatedCountry_ShouldNotPublishEvent() {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);

        // When
        eventPublisherService.publishCountryUpdated(null, previousCountry);

        // Then
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void publishCountryUpdated_WithNullUpdatedCountryId_ShouldNotPublishEvent() {
        // Given
        Country updatedCountry = CountryTestDataFactory.createNorway();
        updatedCountry.setId(null);
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);

        // When
        eventPublisherService.publishCountryUpdated(updatedCountry, previousCountry);

        // Then
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void publishCountryUpdated_WithNullPreviousCountry_ShouldStillPublishEvent() throws Exception {
        // Given
        Country updatedCountry = CountryTestDataFactory.createNorwayWithId(1L);
        String expectedJson = "{\"country\":\"updatedCountryJson\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryUpdated(updatedCountry, null);

        // Then
        verify(restTemplate).postForEntity(anyString(), any(), eq(Object.class));
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void publishCountryDeleted_WithNullCountry_ShouldNotPublishEvent() {
        // When
        eventPublisherService.publishCountryDeleted(null);

        // Then
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void publishCountryDeleted_WithNullCountryId_ShouldNotPublishEvent() {
        // Given
        Country testCountry = CountryTestDataFactory.createNorway();
        testCountry.setId(null);

        // When
        eventPublisherService.publishCountryDeleted(testCountry);

        // Then
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(objectMapper);
    }
}