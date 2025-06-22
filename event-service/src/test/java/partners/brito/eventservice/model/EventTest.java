package partners.brito.eventservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void onCreate_ShouldSetTimestampAndVersionIfNull() {
        // Given
        Event event = Event.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .build();

        // When
        event.onCreate(); // Manually call @PrePersist method for testing

        // Then
        assertNotNull(event.getTimestamp());
        assertEquals("1.0", event.getVersion());
    }

    @Test
    void onCreate_ShouldNotOverrideExistingTimestampAndVersion() {
        // Given
        LocalDateTime existingTimestamp = LocalDateTime.of(2023, 1, 1, 12, 0);
        Event event = Event.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .timestamp(existingTimestamp)
                .version("2.0")
                .build();

        // When
        event.onCreate();

        // Then
        assertEquals(existingTimestamp, event.getTimestamp());
        assertEquals("2.0", event.getVersion());
    }

    @Test
    void builder_ShouldCreateEventWithAllFields() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        Event event = Event.builder()
                .id(1L)
                .eventType("COUNTRY_UPDATED")
                .source("country-service")
                .entityId(456L)
                .data("{\"name\":\"Spain\"}")
                .timestamp(timestamp)
                .version("1.5")
                .build();

        // Then
        assertEquals(1L, event.getId());
        assertEquals("COUNTRY_UPDATED", event.getEventType());
        assertEquals("country-service", event.getSource());
        assertEquals(456L, event.getEntityId());
        assertEquals("{\"name\":\"Spain\"}", event.getData());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("1.5", event.getVersion());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyEvent() {
        // When
        Event event = new Event();

        // Then
        assertNotNull(event);
        assertNull(event.getId());
        assertNull(event.getEventType());
        assertNull(event.getSource());
        assertNull(event.getEntityId());
        assertNull(event.getData());
        assertNull(event.getTimestamp());
        assertNull(event.getVersion());
    }

    @Test
    void allArgsConstructor_ShouldCreateEventWithAllFields() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        Event event = new Event(1L, "COUNTRY_DELETED", "country-service",
                789L, "{\"id\":789}", timestamp, "1.0");

        // Then
        assertEquals(1L, event.getId());
        assertEquals("COUNTRY_DELETED", event.getEventType());
        assertEquals("country-service", event.getSource());
        assertEquals(789L, event.getEntityId());
        assertEquals("{\"id\":789}", event.getData());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("1.0", event.getVersion());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        Event event = new Event();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        event.setId(2L);
        event.setEventType("USER_CREATED");
        event.setSource("user-service");
        event.setEntityId(999L);
        event.setData("{\"username\":\"john\"}");
        event.setTimestamp(timestamp);
        event.setVersion("2.1");

        // Then
        assertEquals(2L, event.getId());
        assertEquals("USER_CREATED", event.getEventType());
        assertEquals("user-service", event.getSource());
        assertEquals(999L, event.getEntityId());
        assertEquals("{\"username\":\"john\"}", event.getData());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("2.1", event.getVersion());
    }

    @Test
    void equals_ShouldWorkCorrectlyForSameEvents() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Event event1 = Event.builder()
                .id(1L)
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .timestamp(timestamp)
                .version("1.0")
                .build();

        Event event2 = Event.builder()
                .id(1L)
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .timestamp(timestamp)
                .version("1.0")
                .build();

        // Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalseForDifferentEvents() {
        // Given
        Event event1 = Event.builder()
                .id(1L)
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .eventType("COUNTRY_UPDATED")
                .source("country-service")
                .build();

        // Then
        assertNotEquals(event1, event2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Given
        Event event = Event.builder()
                .id(1L)
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        String toString = event.toString();

        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("eventType=COUNTRY_CREATED"));
        assertTrue(toString.contains("source=country-service"));
        assertTrue(toString.contains("entityId=123"));
        assertTrue(toString.contains("data={\"name\":\"Portugal\"}"));
        assertTrue(toString.contains("version=1.0"));
    }
}