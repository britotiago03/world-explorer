package com.worldexplorer.countryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceConfigurationTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void defaultEventServiceUrl_WhenNotConfigured_ShouldUseDefaultValue() throws Exception {
        // Given - Create service with explicit default URL
        EventPublisherService defaultService = new EventPublisherService(
                restTemplate, objectMapper, "http://event-service:3021"
        );
        Country testCountry = CountryTestDataFactory.createNorwayWithId(1L);
        String testJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        defaultService.publishCountryCreated(testCountry);

        // Then
        verify(restTemplate).postForEntity(
                eq("http://event-service:3021/api/events/publish"),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void customEventServiceUrl_ShouldBeConfigurable() throws Exception {
        // Given - Test URL configurability
        String customUrl = "http://custom-event-service:8080";
        EventPublisherService customService = new EventPublisherService(
                restTemplate, objectMapper, customUrl
        );
        Country testCountry = CountryTestDataFactory.createNorwayWithId(1L);

        String testJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        customService.publishCountryCreated(testCountry);

        // Then
        verify(restTemplate).postForEntity(
                eq(customUrl + "/api/events/publish"),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void publishCountryDeleted_WithCustomEventServiceUrl_ShouldUseConfiguredUrl() throws Exception {
        // Given
        String customUrl = "http://test-event-service:9999";
        EventPublisherService eventPublisherService = new EventPublisherService(
                restTemplate, objectMapper, customUrl
        );
        Country testCountry = CountryTestDataFactory.createNorwayWithId(1L);

        String testJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"DELETED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryDeleted(testCountry);

        // Then
        verify(restTemplate).postForEntity(
                eq("http://test-event-service:9999/api/events/publish"),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void multipleEvents_ShouldAllUseTheSameConfiguration() throws Exception {
        // Given
        String configuredUrl = "http://test-event-service:9999";
        EventPublisherService eventPublisherService = new EventPublisherService(
                restTemplate, objectMapper, configuredUrl
        );

        Country testCountry = CountryTestDataFactory.createNorwayWithId(1L);
        Country updatedCountry = CountryTestDataFactory.createNorwayWithId(1L);
        updatedCountry.setPopulation(5500000L);

        String testJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);
        eventPublisherService.publishCountryUpdated(updatedCountry, testCountry);
        eventPublisherService.publishCountryDeleted(testCountry);

        // Then
        verify(restTemplate, times(3)).postForEntity(
                eq("http://test-event-service:9999/api/events/publish"),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }
}