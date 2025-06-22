package partners.brito.eventservice.service;

import partners.brito.eventservice.model.Event;
import partners.brito.eventservice.model.EventRequest;
import partners.brito.eventservice.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

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
    void createEvent_ShouldCreateAndSaveEvent() {
        // Given
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.createEvent(testEventRequest);

        // Then
        assertNotNull(result);
        assertEquals("COUNTRY_CREATED", result.getEventType());
        assertEquals("country-service", result.getSource());
        assertEquals(123L, result.getEntityId());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createEvent_WithNullVersion_ShouldSetDefaultVersion() {
        // Given
        testEventRequest.setVersion(null);
        Event expectedEvent = Event.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();
        when(eventRepository.save(any(Event.class))).thenReturn(expectedEvent);

        // When
        Event result = eventService.createEvent(testEventRequest);

        // Then
        assertEquals("1.0", result.getVersion());
        verify(eventRepository).save(argThat(event -> "1.0".equals(event.getVersion())));
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() {
        // Given
        List<Event> expectedEvents = Collections.singletonList(testEvent);
        when(eventRepository.findAll()).thenReturn(expectedEvents);

        // When
        List<Event> result = eventService.getAllEvents();

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository).findAll();
    }

    @Test
    void getEventById_WhenExists_ShouldReturnEvent() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        Optional<Event> result = eventService.getEventById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEvent, result.get());
        verify(eventRepository).findById(1L);
    }

    @Test
    void getEventById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Event> result = eventService.getEventById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(eventRepository).findById(999L);
    }

    @Test
    void getEventsByType_ShouldReturnEventsByType() {
        // Given
        List<Event> expectedEvents = Collections.singletonList(testEvent);
        when(eventRepository.findByEventTypeOrderByTimestampDesc("COUNTRY_CREATED"))
                .thenReturn(expectedEvents);

        // When
        List<Event> result = eventService.getEventsByType("COUNTRY_CREATED");

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository).findByEventTypeOrderByTimestampDesc("COUNTRY_CREATED");
    }

    @Test
    void getEventsBySource_ShouldReturnEventsBySource() {
        // Given
        List<Event> expectedEvents = Collections.singletonList(testEvent);
        when(eventRepository.findBySourceOrderByTimestampDesc("country-service"))
                .thenReturn(expectedEvents);

        // When
        List<Event> result = eventService.getEventsBySource("country-service");

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository).findBySourceOrderByTimestampDesc("country-service");
    }

    @Test
    void getEventsByEntityId_ShouldReturnEventsByEntityId() {
        // Given
        List<Event> expectedEvents = Collections.singletonList(testEvent);
        when(eventRepository.findByEntityIdOrderByTimestampDesc(123L))
                .thenReturn(expectedEvents);

        // When
        List<Event> result = eventService.getEventsByEntityId(123L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository).findByEntityIdOrderByTimestampDesc(123L);
    }

    @Test
    void getRecentEvents_ShouldReturnLimitedEvents() {
        // Given
        List<Event> expectedEvents = Collections.singletonList(testEvent);
        when(eventRepository.findRecentEvents(PageRequest.of(0, 5)))
                .thenReturn(expectedEvents);

        // When
        List<Event> result = eventService.getRecentEvents(5);

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository).findRecentEvents(PageRequest.of(0, 5));
    }

    @Test
    void deleteEvent_ShouldCallRepository() {
        // When
        eventService.deleteEvent(1L);

        // Then
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void existsById_ShouldReturnRepositoryResult() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = eventService.existsById(1L);

        // Then
        assertTrue(result);
        verify(eventRepository).existsById(1L);
    }
}