package com.worldexplorer.countryservice.service;

import com.worldexplorer.countryservice.model.Country;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EventPublisherService {

    private static final String SOURCE_SERVICE = "country-service";
    private static final String EVENT_VERSION = "1.0";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String eventServiceUrl;

    public EventPublisherService(RestTemplate restTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${event.service.url:http://event-service:3021}") String eventServiceUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.eventServiceUrl = eventServiceUrl;
    }

    @Transactional(readOnly = true)
    public void publishCountryCreated(Country country) {
        if (country == null || country.getId() == null) {
            log.warn("Cannot publish COUNTRY_CREATED event: country or country ID is null");
            return;
        }

        try {
            // Force lazy loading within transaction
            Country detachedCountry = createDetachedCountry(country);
            Map<String, Object> eventData = createEventData(detachedCountry, "CREATED");
            publishEvent("COUNTRY_CREATED", country.getId(), eventData);
            log.info("Published COUNTRY_CREATED event for country: {} (ID: {})",
                    country.getName(), country.getId());
        } catch (Exception e) {
            log.error("Failed to publish COUNTRY_CREATED event for country: {} (ID: {})",
                    country.getName(), country.getId(), e);
            // Don't re-throw - we don't want event publishing failures to break the main operation
        }
    }

    @Transactional(readOnly = true)
    public void publishCountryUpdated(Country updatedCountry, Country previousCountry) {
        if (updatedCountry == null || updatedCountry.getId() == null) {
            log.warn("Cannot publish COUNTRY_UPDATED event: updated country or country ID is null");
            return;
        }

        try {
            // Force lazy loading within transaction
            Country detachedUpdatedCountry = createDetachedCountry(updatedCountry);
            Country detachedPreviousCountry = previousCountry != null ? createDetachedCountry(previousCountry) : null;

            Map<String, Object> eventData = createUpdateEventData(detachedUpdatedCountry, detachedPreviousCountry);
            publishEvent("COUNTRY_UPDATED", updatedCountry.getId(), eventData);
            log.info("Published COUNTRY_UPDATED event for country: {} (ID: {})",
                    updatedCountry.getName(), updatedCountry.getId());
        } catch (Exception e) {
            log.error("Failed to publish COUNTRY_UPDATED event for country: {} (ID: {})",
                    updatedCountry.getName(), updatedCountry.getId(), e);
            // Don't re-throw - we don't want event publishing failures to break the main operation
        }
    }

    @Transactional(readOnly = true)
    public void publishCountryDeleted(Country country) {
        if (country == null || country.getId() == null) {
            log.warn("Cannot publish COUNTRY_DELETED event: country or country ID is null");
            return;
        }

        try {
            // Force lazy loading within transaction
            Country detachedCountry = createDetachedCountry(country);
            Map<String, Object> eventData = createEventData(detachedCountry, "DELETED");
            publishEvent("COUNTRY_DELETED", country.getId(), eventData);
            log.info("Published COUNTRY_DELETED event for country: {} (ID: {})",
                    country.getName(), country.getId());
        } catch (Exception e) {
            log.error("Failed to publish COUNTRY_DELETED event for country: {} (ID: {})",
                    country.getName(), country.getId(), e);
            // Don't re-throw - we don't want event publishing failures to break the main operation
        }
    }

    /**
     * Creates a detached copy of the country with all lazy-loaded collections initialized
     */
    private Country createDetachedCountry(Country country) {
        Country detached = new Country();
        detached.setId(country.getId());
        detached.setName(country.getName());
        detached.setOfficialName(country.getOfficialName());
        detached.setIsoCode(country.getIsoCode());
        detached.setCapital(country.getCapital());
        detached.setDemonym(country.getDemonym());
        detached.setArea(country.getArea());
        detached.setWaterPercent(country.getWaterPercent());
        detached.setPopulation(country.getPopulation());
        detached.setPopulationDensity(country.getPopulationDensity());
        detached.setCallingCode(country.getCallingCode());
        detached.setInternetTld(country.getInternetTld());
        detached.setDateFormat(country.getDateFormat());
        detached.setTimezone(country.getTimezone());
        detached.setSummerTimezone(country.getSummerTimezone());
        detached.setCurrency(country.getCurrency());
        detached.setFlagUrl(country.getFlagUrl());
        detached.setCoatOfArmsUrl(country.getCoatOfArmsUrl());

        // Force initialization of lazy collections and copy them
        try {
            detached.setNativeNames(country.getNativeNames() != null ?
                    java.util.List.copyOf(country.getNativeNames()) : java.util.List.of());
            detached.setOfficialLanguages(country.getOfficialLanguages() != null ?
                    java.util.List.copyOf(country.getOfficialLanguages()) : java.util.List.of());
            detached.setRecognizedLanguages(country.getRecognizedLanguages() != null ?
                    java.util.List.copyOf(country.getRecognizedLanguages()) : java.util.List.of());
        } catch (Exception e) {
            log.warn("Failed to initialize lazy collections for country {}, using empty lists", country.getId());
            detached.setNativeNames(java.util.List.of());
            detached.setOfficialLanguages(java.util.List.of());
            detached.setRecognizedLanguages(java.util.List.of());
        }

        return detached;
    }

    private Map<String, Object> createEventData(Country country, String action) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("country", country);
        eventData.put("action", action);
        eventData.put("timestamp", System.currentTimeMillis());
        return eventData;
    }

    private Map<String, Object> createUpdateEventData(Country updatedCountry, Country previousCountry) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("country", updatedCountry);
        if (previousCountry != null) {
            eventData.put("previousCountry", previousCountry);
        }
        eventData.put("action", "UPDATED");
        eventData.put("timestamp", System.currentTimeMillis());
        return eventData;
    }

    private void publishEvent(String eventType, Long entityId, Map<String, Object> eventData) {
        try {
            String dataJson = serializeEventData(eventData);

            Map<String, Object> eventRequest = createEventRequest(eventType, entityId, dataJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(eventRequest, headers);

            String url = eventServiceUrl + "/api/events/publish";

            ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Successfully published event {} to {}", eventType, url);
            } else {
                log.warn("Event service returned non-success status: {} for event {}",
                        response.getStatusCode(), eventType);
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event data for event type: {}", eventType, e);
            throw new RuntimeException("Event serialization failed", e);
        } catch (RestClientException e) {
            log.error("Failed to send event {} to event service at {}", eventType, eventServiceUrl, e);
            throw new RuntimeException("Event publishing failed", e);
        } catch (Exception e) {
            log.error("Unexpected error publishing event {} to event service", eventType, e);
            throw new RuntimeException("Unexpected event publishing error", e);
        }
    }

    private String serializeEventData(Map<String, Object> eventData) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(eventData);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization failed for event data: {}", eventData, e);
            throw e;
        }
    }

    private Map<String, Object> createEventRequest(String eventType, Long entityId, String dataJson) {
        Map<String, Object> eventRequest = new HashMap<>();
        eventRequest.put("eventType", eventType);
        eventRequest.put("source", SOURCE_SERVICE);
        eventRequest.put("entityId", entityId);
        eventRequest.put("data", dataJson);
        eventRequest.put("version", EVENT_VERSION);
        return eventRequest;
    }
}