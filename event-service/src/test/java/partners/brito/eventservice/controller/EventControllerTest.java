package partners.brito.eventservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import partners.brito.eventservice.model.Event;
import partners.brito.eventservice.model.EventRequest;
import partners.brito.eventservice.service.EventBroadcastService;
import partners.brito.eventservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @SuppressWarnings("unused") // Injected by Spring Boot test framework
    private EventService eventService;

    @MockitoBean
    @SuppressWarnings("unused") // Injected by Spring Boot test framework
    private EventBroadcastService broadcastService;

    @Autowired
    private ObjectMapper objectMapper;

    private Event testEvent;
    private EventRequest testEventRequest;

    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
                .id(1L)
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .timestamp(LocalDateTime.now())
                .build();

        testEventRequest = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();
    }

    @Test
    void publishEvent_WithValidRequest_ShouldReturnEvent() throws Exception {
        // Given
        when(eventService.createEvent(any(EventRequest.class))).thenReturn(testEvent);

        // When & Then
        mockMvc.perform(post("/api/events/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventType").value("COUNTRY_CREATED"))
                .andExpect(jsonPath("$.source").value("country-service"))
                .andExpect(jsonPath("$.entityId").value(123));

        verify(eventService).createEvent(any(EventRequest.class));
        verify(broadcastService).broadcastEvent(testEvent);
    }

    @Test
    void publishEvent_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given - EventRequest with missing required fields
        EventRequest invalidRequest = EventRequest.builder()
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .build();

        // When & Then
        mockMvc.perform(post("/api/events/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventRequest.class));
        verify(broadcastService, never()).broadcastEvent(any(Event.class));
    }

    @Test
    void subscribeToEvents_ShouldReturnEventStream() throws Exception {
        // Given
        when(broadcastService.getEventStream()).thenReturn(Flux.just(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/subscribe")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM));

        verify(broadcastService).getEventStream();
    }

    @Test
    void subscribeToEventsByType_ShouldReturnFilteredStream() throws Exception {
        // Given
        when(broadcastService.getEventStreamForType("COUNTRY_CREATED"))
                .thenReturn(Flux.just(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/subscribe/COUNTRY_CREATED")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM));

        verify(broadcastService).getEventStreamForType("COUNTRY_CREATED");
    }

    @Test
    void subscribeToEventsBySource_ShouldReturnFilteredStream() throws Exception {
        // Given
        when(broadcastService.getEventStreamForSource("country-service"))
                .thenReturn(Flux.just(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/subscribe/source/country-service")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM));

        verify(broadcastService).getEventStreamForSource("country-service");
    }

    @Test
    void getAllEvents_ShouldReturnEventList() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getAllEvents()).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventType").value("COUNTRY_CREATED"));

        verify(eventService).getAllEvents();
    }

    @Test
    void getEventById_WhenExists_ShouldReturnEvent() throws Exception {
        // Given
        when(eventService.getEventById(1L)).thenReturn(Optional.of(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventType").value("COUNTRY_CREATED"));

        verify(eventService).getEventById(1L);
    }

    @Test
    void getEventById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(eventService.getEventById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/events/999"))
                .andExpect(status().isNotFound());

        verify(eventService).getEventById(999L);
    }

    @Test
    void getEventsByType_ShouldReturnFilteredEvents() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getEventsByType("COUNTRY_CREATED")).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/type/COUNTRY_CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eventType").value("COUNTRY_CREATED"));

        verify(eventService).getEventsByType("COUNTRY_CREATED");
    }

    @Test
    void getEventsBySource_ShouldReturnFilteredEvents() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getEventsBySource("country-service")).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/source/country-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].source").value("country-service"));

        verify(eventService).getEventsBySource("country-service");
    }

    @Test
    void getEventsByEntityId_ShouldReturnFilteredEvents() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getEventsByEntityId(123L)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/entity/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].entityId").value(123));

        verify(eventService).getEventsByEntityId(123L);
    }

    @Test
    void getRecentEvents_ShouldReturnLimitedEvents() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getRecentEvents(5)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/recent").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(eventService).getRecentEvents(5);
    }

    @Test
    void getRecentEvents_WithDefaultLimit_ShouldUseDefault() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getRecentEvents(10)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/recent"))
                .andExpect(status().isOk());

        verify(eventService).getRecentEvents(10);
    }

    @Test
    void getEventsAfter_ShouldReturnFilteredEvents() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.getEventsAfter(any(LocalDateTime.class))).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/since")
                        .param("timestamp", "2024-01-01T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(eventService).getEventsAfter(any(LocalDateTime.class));
    }

    @Test
    void health_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/events/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Event Service is running"));
    }

    @Test
    void deleteEvent_WhenExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(eventService.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());

        verify(eventService).existsById(1L);
        verify(eventService).deleteEvent(1L);
    }

    @Test
    void deleteEvent_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(eventService.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/events/999"))
                .andExpect(status().isNotFound());

        verify(eventService).existsById(999L);
        verify(eventService, never()).deleteEvent(999L);
    }
}