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
 * Unit tests for EventPublisherService focusing on successful execution scenarios.
 */
@ExtendWith(MockitoExtension.class)
class EventPublisherServiceHappyPathTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Map<String, Object>> eventDataCaptor;

    private EventPublisherService eventPublisherService;

    private static final String EVENT_SERVICE_URL = "http://test-event-service:3021";

    @BeforeEach
    void setUp() {
        eventPublisherService = new EventPublisherService(restTemplate, objectMapper, EVENT_SERVICE_URL);
    }

    @Test
    void publishCountryCreated_WithCompleteCountry_ShouldSerializeAndPublishSuccessfully() throws JsonProcessingException {
        // Given
        Country completeCountry = CountryTestDataFactory.createNorwayWithId(1L);
        String expectedJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"CREATED\",\"timestamp\":123456789}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>("Event published", HttpStatus.OK);

        when(objectMapper.writeValueAsString(eventDataCaptor.capture())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(completeCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));

        Map<String, Object> capturedEventData = eventDataCaptor.getValue();
        assertThat(capturedEventData.get("country")).isEqualTo(completeCountry);
        assertThat(capturedEventData.get("action")).isEqualTo("CREATED");
        assertThat(capturedEventData).containsKey("timestamp");
    }

    @Test
    void publishCountryUpdated_WithBothCountries_ShouldSerializeAndPublishSuccessfully() throws JsonProcessingException {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);
        previousCountry.setPopulation(5000000L);

        Country updatedCountry = CountryTestDataFactory.createNorwayWithId(1L);
        updatedCountry.setPopulation(5500000L);

        String expectedJson = "{\"country\":{\"name\":\"Norway\"},\"previousCountry\":{\"name\":\"Norway\"},\"action\":\"UPDATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>("Event published", HttpStatus.OK);

        when(objectMapper.writeValueAsString(eventDataCaptor.capture())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryUpdated(updatedCountry, previousCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));

        Map<String, Object> capturedEventData = eventDataCaptor.getValue();
        assertThat(capturedEventData.get("country")).isEqualTo(updatedCountry);
        assertThat(capturedEventData.get("previousCountry")).isEqualTo(previousCountry);
        assertThat(capturedEventData.get("action")).isEqualTo("UPDATED");
        assertThat(capturedEventData).containsKey("timestamp");
    }

    @Test
    void publishCountryDeleted_WithCompleteCountry_ShouldSerializeAndPublishSuccessfully() throws JsonProcessingException {
        // Given
        Country deletedCountry = CountryTestDataFactory.createDenmarkWithId(3L);
        String expectedJson = "{\"country\":{\"name\":\"Denmark\"},\"action\":\"DELETED\",\"timestamp\":123456789}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>("Event published", HttpStatus.OK);

        when(objectMapper.writeValueAsString(eventDataCaptor.capture())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryDeleted(deletedCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));

        Map<String, Object> capturedEventData = eventDataCaptor.getValue();
        assertThat(capturedEventData.get("country")).isEqualTo(deletedCountry);
        assertThat(capturedEventData.get("action")).isEqualTo("DELETED");
        assertThat(capturedEventData).containsKey("timestamp");
    }

    @Test
    void publishCountryCreated_WithMinimalCountry_ShouldStillPublishSuccessfully() throws JsonProcessingException {
        // Given
        Country minimalCountry = CountryTestDataFactory.createMinimalWithId(100L, "TestCountry", "Test Country Official");
        String expectedJson = "{\"country\":{\"name\":\"TestCountry\"},\"action\":\"CREATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>("Event published", HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(minimalCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishEvents_WithDifferentCountries_ShouldHandleAllTypesSuccessfully() throws JsonProcessingException {
        // Given
        Country norwayCountry = CountryTestDataFactory.createNorwayWithId(1L);
        Country swedenCountry = CountryTestDataFactory.createSwedenWithId(2L);
        Country denmarkCountry = CountryTestDataFactory.createDenmarkWithId(3L);

        String expectedJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>("Event published", HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(norwayCountry);
        eventPublisherService.publishCountryUpdated(swedenCountry, norwayCountry);
        eventPublisherService.publishCountryDeleted(denmarkCountry);

        // Then
        verify(objectMapper, times(3)).writeValueAsString(any());
        verify(restTemplate, times(3)).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishEvents_WithAcceptedHttpStatus_ShouldHandleSuccessfully() throws JsonProcessingException {
        // Given
        Country testCountry = CountryTestDataFactory.createNorwayWithId(1L);
        String expectedJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> acceptedResponse = new ResponseEntity<>("Event accepted", HttpStatus.ACCEPTED);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(acceptedResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void publishEvents_WithCreatedHttpStatus_ShouldHandleSuccessfully() throws JsonProcessingException {
        // Given
        Country testCountry = CountryTestDataFactory.createNorwayWithId(1L);
        String expectedJson = "{\"test\":\"data\"}";
        ResponseEntity<Object> createdResponse = new ResponseEntity<>("Event created", HttpStatus.CREATED);

        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(createdResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(objectMapper).writeValueAsString(any());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Object.class));
    }
}