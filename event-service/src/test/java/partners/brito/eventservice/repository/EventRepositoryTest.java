package partners.brito.eventservice.repository;

import partners.brito.eventservice.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent1;
    private Event testEvent2;
    private Event testEvent3;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testEvent1 = Event.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .timestamp(now.minusHours(1))
                .build();

        testEvent2 = Event.builder()
                .eventType("COUNTRY_UPDATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal Updated\"}")
                .version("1.1")
                .timestamp(now.minusMinutes(30))
                .build();

        testEvent3 = Event.builder()
                .eventType("USER_CREATED")
                .source("user-service")
                .entityId(456L)
                .data("{\"name\":\"John\"}")
                .version("1.0")
                .timestamp(now.minusMinutes(15))
                .build();

        // Save test events
        testEvent1 = entityManager.persistAndFlush(testEvent1);
        testEvent2 = entityManager.persistAndFlush(testEvent2);
        testEvent3 = entityManager.persistAndFlush(testEvent3);
    }

    @Test
    void findByEventTypeOrderByTimestampDesc_ShouldReturnEventsOfSpecificType() {
        // When
        List<Event> result = eventRepository.findByEventTypeOrderByTimestampDesc("COUNTRY_CREATED");

        // Then
        assertEquals(1, result.size());
        assertEquals("COUNTRY_CREATED", result.get(0).getEventType());
        assertEquals(testEvent1.getId(), result.get(0).getId());
    }

    @Test
    void findBySourceOrderByTimestampDesc_ShouldReturnEventsFromSpecificSource() {
        // When
        List<Event> result = eventRepository.findBySourceOrderByTimestampDesc("country-service");

        // Then
        assertEquals(2, result.size());
        // Should be ordered by timestamp desc
        assertEquals(testEvent2.getId(), result.get(0).getId()); // More recent
        assertEquals(testEvent1.getId(), result.get(1).getId()); // Older
    }

    @Test
    void findByEntityIdOrderByTimestampDesc_ShouldReturnEventsForSpecificEntity() {
        // When
        List<Event> result = eventRepository.findByEntityIdOrderByTimestampDesc(123L);

        // Then
        assertEquals(2, result.size());
        assertEquals(testEvent2.getId(), result.get(0).getId()); // More recent
        assertEquals(testEvent1.getId(), result.get(1).getId()); // Older
    }

    @Test
    void findByTimestampAfterOrderByTimestampDesc_ShouldReturnEventsAfterTimestamp() {
        // Given
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(45);

        // When
        List<Event> result = eventRepository.findByTimestampAfterOrderByTimestampDesc(cutoffTime);

        // Then
        assertEquals(2, result.size()); // testEvent2 and testEvent3
        assertEquals(testEvent3.getId(), result.get(0).getId()); // Most recent
        assertEquals(testEvent2.getId(), result.get(1).getId()); // Second most recent
    }

    @Test
    void findByEventTypeAndSourceOrderByTimestampDesc_ShouldReturnFilteredEvents() {
        // When
        List<Event> result = eventRepository.findByEventTypeAndSourceOrderByTimestampDesc(
                "COUNTRY_UPDATED", "country-service");

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent2.getId(), result.get(0).getId());
    }

    @Test
    void findRecentEvents_ShouldReturnLimitedEvents() {
        // When
        List<Event> result = eventRepository.findRecentEvents(PageRequest.of(0, 2));

        // Then
        assertEquals(2, result.size());
        // Should be ordered by timestamp desc
        assertEquals(testEvent3.getId(), result.get(0).getId()); // Most recent
        assertEquals(testEvent2.getId(), result.get(1).getId()); // Second most recent
    }

    @Test
    void findEventsBetween_ShouldReturnEventsInTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(2);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(20);

        // When
        List<Event> result = eventRepository.findEventsBetween(startTime, endTime);

        // Then
        assertEquals(2, result.size()); // testEvent1 and testEvent2
        assertEquals(testEvent2.getId(), result.get(0).getId()); // More recent
        assertEquals(testEvent1.getId(), result.get(1).getId()); // Older
    }

    @Test
    void save_ShouldPersistEvent() {
        // Given
        Event newEvent = Event.builder()
                .eventType("TEST_EVENT")
                .source("test-service")
                .entityId(999L)
                .data("{\"test\":\"data\"}")
                .version("1.0")
                .build();

        // When
        Event savedEvent = eventRepository.save(newEvent);

        // Then
        assertNotNull(savedEvent.getId());
        assertNotNull(savedEvent.getTimestamp());
        assertEquals("TEST_EVENT", savedEvent.getEventType());
        assertEquals("test-service", savedEvent.getSource());
        assertEquals("1.0", savedEvent.getVersion());
    }

    @Test
    void findById_ShouldReturnEvent() {
        // When
        var result = eventRepository.findById(testEvent1.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEvent1.getEventType(), result.get().getEventType());
    }

    @Test
    void deleteById_ShouldRemoveEvent() {
        // Given
        Long eventId = testEvent1.getId();
        assertTrue(eventRepository.existsById(eventId));

        // When
        eventRepository.deleteById(eventId);
        entityManager.flush();

        // Then
        assertFalse(eventRepository.existsById(eventId));
    }
}