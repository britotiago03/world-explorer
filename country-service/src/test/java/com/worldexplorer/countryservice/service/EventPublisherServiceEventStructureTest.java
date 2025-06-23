package com.worldexplorer.countryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventPublisherService focusing on event structure and data content.
 */
@ExtendWith(MockitoExtension.class)
class EventPublisherServiceEventStructureTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<HttpEntity<Map<String, Object>>> httpEntityCaptor;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    private EventPublisherService eventPublisherService;
    private Country testCountry;

    private static final String EVENT_SERVICE_URL = "http://test-event-service:3021";

    @BeforeEach
    void setUp() {
        eventPublisherService = new EventPublisherService(restTemplate, objectMapper, EVENT_SERVICE_URL);
        testCountry = CountryTestDataFactory.createNorwayWithId(1L);
    }

    @Test
    void publishCountryCreated_WithValidCountry_ShouldCreateCorrectEventStructure() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\",\"action\":\"CREATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(restTemplate).postForEntity(urlCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        String capturedUrl = urlCaptor.getValue();
        HttpEntity<Map<String, Object>> capturedEntity = httpEntityCaptor.getValue();
        Map<String, Object> eventRequest = capturedEntity.getBody();

        assertThat(capturedUrl).isEqualTo(EVENT_SERVICE_URL + "/api/events/publish");
        assertThat(eventRequest).isNotNull();
        assertThat(eventRequest).containsEntry("eventType", "COUNTRY_CREATED");
        assertThat(eventRequest).containsEntry("source", "country-service");
        assertThat(eventRequest).containsEntry("entityId", testCountry.getId());
        assertThat(eventRequest).containsEntry("version", "1.0");
        assertThat(eventRequest).containsKey("data");
    }

    @Test
    void publishCountryUpdated_WithValidCountries_ShouldCreateCorrectEventStructure() throws JsonProcessingException {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);
        Country updatedCountry = CountryTestDataFactory.createNorwayWithId(1L);
        updatedCountry.setPopulation(5500000L);

        String expectedJson = "{\"country\":\"updatedCountryJson\",\"previousCountry\":\"previousCountryJson\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryUpdated(updatedCountry, previousCountry);

        // Then
        verify(restTemplate).postForEntity(urlCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        String capturedUrl = urlCaptor.getValue();
        HttpEntity<Map<String, Object>> capturedEntity = httpEntityCaptor.getValue();
        Map<String, Object> eventRequest = capturedEntity.getBody();

        assertThat(capturedUrl).isEqualTo(EVENT_SERVICE_URL + "/api/events/publish");
        assertThat(eventRequest).isNotNull();
        assertThat(eventRequest).containsEntry("eventType", "COUNTRY_UPDATED");
        assertThat(eventRequest).containsEntry("source", "country-service");
        assertThat(eventRequest).containsEntry("entityId", updatedCountry.getId());
        assertThat(eventRequest).containsEntry("version", "1.0");
        assertThat(eventRequest).containsKey("data");
    }

    @Test
    void publishCountryDeleted_WithValidCountry_ShouldCreateCorrectEventStructure() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\",\"action\":\"DELETED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryDeleted(testCountry);

        // Then
        verify(restTemplate).postForEntity(urlCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        String capturedUrl = urlCaptor.getValue();
        HttpEntity<Map<String, Object>> capturedEntity = httpEntityCaptor.getValue();
        Map<String, Object> eventRequest = capturedEntity.getBody();

        assertThat(capturedUrl).isEqualTo(EVENT_SERVICE_URL + "/api/events/publish");
        assertThat(eventRequest).isNotNull();
        assertThat(eventRequest).containsEntry("eventType", "COUNTRY_DELETED");
        assertThat(eventRequest).containsEntry("source", "country-service");
        assertThat(eventRequest).containsEntry("entityId", testCountry.getId());
        assertThat(eventRequest).containsEntry("version", "1.0");
        assertThat(eventRequest).containsKey("data");
    }

    @Test
    void publishEvent_ShouldSetCorrectHeaders() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"country\":\"countryJson\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(restTemplate).postForEntity(anyString(), httpEntityCaptor.capture(), eq(Object.class));

        HttpEntity<Map<String, Object>> capturedEntity = httpEntityCaptor.getValue();
        assertThat(capturedEntity.getHeaders().getContentType()).isNotNull();
        assertThat(capturedEntity.getHeaders().getContentType().toString()).isEqualTo("application/json");
    }

    @Test
    void eventDataStructure_ForCreatedEvent_ShouldContainCorrectFields() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(argThat(eventData -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) eventData;
            return data.containsKey("country") &&
                    data.containsKey("action") &&
                    data.containsKey("timestamp") &&
                    "CREATED".equals(data.get("action"));
        }))).thenReturn(expectedJson);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void eventDataStructure_ForUpdatedEvent_ShouldContainCorrectFields() throws JsonProcessingException {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);
        Country updatedCountry = CountryTestDataFactory.createNorwayWithId(1L);
        updatedCountry.setPopulation(5500000L);

        String expectedJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(argThat(eventData -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) eventData;
            return data.containsKey("country") &&
                    data.containsKey("previousCountry") &&
                    data.containsKey("action") &&
                    data.containsKey("timestamp") &&
                    "UPDATED".equals(data.get("action"));
        }))).thenReturn(expectedJson);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryUpdated(updatedCountry, previousCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void eventDataStructure_ForDeletedEvent_ShouldContainCorrectFields() throws JsonProcessingException {
        // Given
        String expectedJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(argThat(eventData -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) eventData;
            return data.containsKey("country") &&
                    data.containsKey("action") &&
                    data.containsKey("timestamp") &&
                    "DELETED".equals(data.get("action"));
        }))).thenReturn(expectedJson);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryDeleted(testCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
    }
}