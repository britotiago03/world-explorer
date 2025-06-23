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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceSerializationTest {

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
    void eventPublisherService_ShouldUseObjectMapperForSerialization() throws Exception {
        // Given
        String testJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"CREATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(restTemplate).postForEntity(
                anyString(),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<Map<String, Object>> capturedEntity = entityCaptor.getValue();
        Map<String, Object> body = capturedEntity.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("data")).isEqualTo(testJson);

        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void eventData_ShouldContainTimestamp() throws Exception {
        // Given
        long beforeCall = System.currentTimeMillis();
        String testJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"CREATED\",\"timestamp\":123456789}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        ArgumentCaptor<Map> eventDataCaptor = ArgumentCaptor.forClass(Map.class);
        when(objectMapper.writeValueAsString(eventDataCaptor.capture())).thenReturn(testJson);

        // When
        eventPublisherService.publishCountryCreated(testCountry);
        long afterCall = System.currentTimeMillis();

        // Then
        Map<String, Object> eventData = eventDataCaptor.getValue();
        Object timestamp = eventData.get("timestamp");

        assertThat(timestamp).isInstanceOf(Long.class);
        long eventTime = ((Long) timestamp);
        assertThat(eventTime).isBetween(beforeCall, afterCall);
    }

    @Test
    void httpHeaders_ShouldBeSetCorrectly() throws Exception {
        // Given
        String testJson = "{\"country\":{\"name\":\"Norway\"},\"action\":\"CREATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(objectMapper.writeValueAsString(any())).thenReturn(testJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        // When
        eventPublisherService.publishCountryCreated(testCountry);

        // Then
        verify(restTemplate).postForEntity(
                anyString(),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<Map<String, Object>> capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getHeaders().getContentType().toString())
                .isEqualTo("application/json");
    }

    @Test
    void serialization_ShouldHandleComplexCountryObjects() throws Exception {
        // Given
        Country complexCountry = CountryTestDataFactory.createSwedenWithId(2L);
        String complexJson = "{\"country\":{\"name\":\"Sweden\",\"languages\":[\"Swedish\",\"Finnish\"]},\"action\":\"CREATED\"}";
        ResponseEntity<Object> successResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(objectMapper.writeValueAsString(any())).thenReturn(complexJson);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        ArgumentCaptor<Map> eventDataCaptor = ArgumentCaptor.forClass(Map.class);
        when(objectMapper.writeValueAsString(eventDataCaptor.capture())).thenReturn(complexJson);

        // When
        eventPublisherService.publishCountryCreated(complexCountry);

        // Then
        Map<String, Object> eventData = eventDataCaptor.getValue();
        assertThat(eventData.get("country")).isEqualTo(complexCountry);
        assertThat(eventData.get("action")).isEqualTo("CREATED");
        verify(objectMapper).writeValueAsString(eventData);
    }
}