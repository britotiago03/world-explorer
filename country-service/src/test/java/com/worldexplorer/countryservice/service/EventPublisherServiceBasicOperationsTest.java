package com.worldexplorer.countryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceBasicOperationsTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private EventPublisherService eventPublisherService;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        eventPublisherService = new EventPublisherService(
                restTemplate,
                objectMapper,
                "http://test-event-service:9999"
        );
        testCountry = CountryTestDataFactory.createNorwayWithId(1L);
    }

    @Test
    void contextLoads() {
        assertThat(eventPublisherService).isNotNull();
        assertThat(restTemplate).isNotNull();
        assertThat(objectMapper).isNotNull();
    }

    @Test
    void publishCountryCreated_ShouldPublishWithCorrectEventStructure() throws Exception {
        // Given
        String testJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"CREATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        assertThatNoException().isThrownBy(() ->
                eventPublisherService.publishCountryCreated(testCountry)
        );

        // Then
        verify(restTemplate).postForEntity(
                eq("http://test-event-service:9999/api/events/publish"),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<Map<String, Object>> capturedEntity = entityCaptor.getValue();
        Map<String, Object> body = capturedEntity.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("eventType")).isEqualTo("COUNTRY_CREATED");
        assertThat(body.get("source")).isEqualTo("country-service");
        assertThat(body.get("entityId")).isEqualTo(testCountry.getId());
        assertThat(body.get("version")).isEqualTo("1.0");
        assertThat(body).containsKey("data");
    }

    @Test
    void publishCountryUpdated_ShouldPublishWithCorrectEventStructure() throws Exception {
        // Given
        Country previousCountry = CountryTestDataFactory.createNorwayWithId(1L);
        previousCountry.setPopulation(5000000L);

        Country updatedCountry = CountryTestDataFactory.createNorwayWithId(1L);
        updatedCountry.setPopulation(5500000L);

        String testJson = "{\"country\":{\"name\":\"Norway\"},\"previousCountry\":{\"name\":\"Norway\"},\"action\":\"UPDATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        assertThatNoException().isThrownBy(() ->
                eventPublisherService.publishCountryUpdated(updatedCountry, previousCountry)
        );

        // Then
        verify(restTemplate).postForEntity(
                eq("http://test-event-service:9999/api/events/publish"),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<Map<String, Object>> capturedEntity = entityCaptor.getValue();
        Map<String, Object> body = capturedEntity.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("eventType")).isEqualTo("COUNTRY_UPDATED");
        assertThat(body.get("source")).isEqualTo("country-service");
        assertThat(body.get("entityId")).isEqualTo(updatedCountry.getId());
        assertThat(body.get("version")).isEqualTo("1.0");
        assertThat(body).containsKey("data");
        assertThat(body.get("data")).isEqualTo(testJson);
    }

    @Test
    void publishCountryDeleted_ShouldPublishWithCorrectEventStructure() throws Exception {
        // Given
        String testJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"DELETED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        assertThatNoException().isThrownBy(() ->
                eventPublisherService.publishCountryDeleted(testCountry)
        );

        // Then
        verify(restTemplate).postForEntity(
                eq("http://test-event-service:9999/api/events/publish"),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<Map<String, Object>> capturedEntity = entityCaptor.getValue();
        Map<String, Object> body = capturedEntity.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("eventType")).isEqualTo("COUNTRY_DELETED");
        assertThat(body.get("source")).isEqualTo("country-service");
        assertThat(body.get("entityId")).isEqualTo(testCountry.getId());
        assertThat(body.get("version")).isEqualTo("1.0");
        assertThat(body).containsKey("data");
    }
}