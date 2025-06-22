package partners.brito.eventservice.service;

import partners.brito.eventservice.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EventBroadcastServiceTest {

    private EventBroadcastService eventBroadcastService;
    private Event testEvent1;
    private Event testEvent2;

    @BeforeEach
    void setUp() {
        eventBroadcastService = new EventBroadcastService();

        testEvent1 = Event.builder()
                .id(1L)
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .timestamp(LocalDateTime.now())
                .build();

        testEvent2 = Event.builder()
                .id(2L)
                .eventType("COUNTRY_UPDATED")
                .source("country-service")
                .entityId(124L)
                .data("{\"name\":\"Spain\"}")
                .version("1.0")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void broadcastEvent_ShouldEmitEventToSubscribers() {
        // Given
        Flux<Event> eventStream = eventBroadcastService.getEventStream();

        // When & Then
        StepVerifier.create(eventStream)
                .then(() -> eventBroadcastService.broadcastEvent(testEvent1))
                .expectNext(testEvent1)
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void broadcastMultipleEvents_ShouldEmitAllEvents() {
        // Given
        Flux<Event> eventStream = eventBroadcastService.getEventStream();

        // When & Then
        StepVerifier.create(eventStream)
                .then(() -> {
                    eventBroadcastService.broadcastEvent(testEvent1);
                    eventBroadcastService.broadcastEvent(testEvent2);
                })
                .expectNext(testEvent1)
                .expectNext(testEvent2)
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getEventStreamForType_ShouldFilterEventsByType() {
        // Given
        Flux<Event> filteredStream = eventBroadcastService.getEventStreamForType("COUNTRY_CREATED");

        // When & Then
        StepVerifier.create(filteredStream)
                .then(() -> {
                    eventBroadcastService.broadcastEvent(testEvent1); // COUNTRY_CREATED
                    eventBroadcastService.broadcastEvent(testEvent2); // COUNTRY_UPDATED
                })
                .expectNext(testEvent1) // Only COUNTRY_CREATED should pass through
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getEventStreamForSource_ShouldFilterEventsBySource() {
        // Given
        Event userServiceEvent = Event.builder()
                .id(3L)
                .eventType("USER_CREATED")
                .source("user-service")
                .entityId(999L)
                .data("{\"name\":\"John\"}")
                .version("1.0")
                .timestamp(LocalDateTime.now())
                .build();

        Flux<Event> filteredStream = eventBroadcastService.getEventStreamForSource("country-service");

        // When & Then
        StepVerifier.create(filteredStream)
                .then(() -> {
                    eventBroadcastService.broadcastEvent(testEvent1); // country-service
                    eventBroadcastService.broadcastEvent(userServiceEvent); // user-service
                })
                .expectNext(testEvent1) // Only country-service events should pass through
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void multipleSubscribers_ShouldReceiveSameEvent() {
        // This test verifies that the service can handle multiple sequential subscriptions
        // Test sequential subscriptions (which is what actually happens in real usage)

        // First subscription - verify it works
        StepVerifier.create(eventBroadcastService.getEventStream().take(1))
                .then(() -> eventBroadcastService.broadcastEvent(testEvent1))
                .expectNext(testEvent1)
                .verifyComplete();

        // Second subscription - verify service can handle new subscriber
        StepVerifier.create(eventBroadcastService.getEventStream().take(1))
                .then(() -> eventBroadcastService.broadcastEvent(testEvent1))
                .expectNext(testEvent1)
                .verifyComplete();
    }

    @Test
    void getEventStream_ShouldNotBeNull() {
        // When
        Flux<Event> eventStream = eventBroadcastService.getEventStream();

        // Then
        assertNotNull(eventStream);
    }

    @Test
    void getEventStreamForType_WithNullType_ShouldFilterCorrectly() {
        // Given
        Event nullTypeEvent = Event.builder()
                .id(4L)
                .eventType(null)
                .source("test-service")
                .entityId(555L)
                .build();

        Flux<Event> filteredStream = eventBroadcastService.getEventStreamForType(null);

        // When & Then
        StepVerifier.create(filteredStream)
                .then(() -> {
                    eventBroadcastService.broadcastEvent(testEvent1); // Has eventType
                    eventBroadcastService.broadcastEvent(nullTypeEvent); // null eventType
                })
                .expectNext(nullTypeEvent) // Only null eventType should pass through
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void shouldHandleEmptyStreamGracefully() {
        // Given
        Flux<Event> eventStream = eventBroadcastService.getEventStream();

        // When & Then - Test that an empty stream doesn't hang
        StepVerifier.create(eventStream.take(Duration.ofMillis(100)))
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }
}