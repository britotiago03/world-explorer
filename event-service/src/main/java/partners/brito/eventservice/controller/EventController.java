package partners.brito.eventservice.controller;

import partners.brito.eventservice.model.Event;
import partners.brito.eventservice.model.EventRequest;
import partners.brito.eventservice.service.EventService;
import partners.brito.eventservice.service.EventBroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
@Slf4j
public class EventController {

    private final EventService eventService;
    private final EventBroadcastService broadcastService;

    public EventController(EventService eventService, EventBroadcastService broadcastService) {
        this.eventService = eventService;
        this.broadcastService = broadcastService;
    }

    // Publish a new event (called by other services like country-service)
    @PostMapping("/publish")
    public ResponseEntity<Event> publishEvent(@Valid @RequestBody EventRequest request) {
        log.info("Received event publish request: type={}, source={}",
                request.getEventType(), request.getSource());

        Event event = eventService.createEvent(request);
        broadcastService.broadcastEvent(event);

        return ResponseEntity.ok(event);
    }

    // Subscribe to all events via Server-Sent Events
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> subscribeToEvents() {
        log.info("New SSE subscription to all events");
        return broadcastService.getEventStream();
    }

    // Subscribe to events of a specific type
    @GetMapping(value = "/subscribe/{eventType}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> subscribeToEventsByType(@PathVariable String eventType) {
        log.info("New SSE subscription to events of type: {}", eventType);
        return broadcastService.getEventStreamForType(eventType);
    }

    // Subscribe to events from a specific source
    @GetMapping(value = "/subscribe/source/{source}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> subscribeToEventsBySource(@PathVariable String source) {
        log.info("New SSE subscription to events from source: {}", source);
        return broadcastService.getEventStreamForSource(source);
    }

    // Get all events (for debugging/admin purposes)
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get events by type
    @GetMapping("/type/{eventType}")
    public List<Event> getEventsByType(@PathVariable String eventType) {
        return eventService.getEventsByType(eventType);
    }

    // Get events by source
    @GetMapping("/source/{source}")
    public List<Event> getEventsBySource(@PathVariable String source) {
        return eventService.getEventsBySource(source);
    }

    // Get events by entity ID
    @GetMapping("/entity/{entityId}")
    public List<Event> getEventsByEntityId(@PathVariable Long entityId) {
        return eventService.getEventsByEntityId(entityId);
    }

    // Get recent events (last N events)
    @GetMapping("/recent")
    public List<Event> getRecentEvents(@RequestParam(defaultValue = "10") int limit) {
        return eventService.getRecentEvents(limit);
    }

    // Get events after a specific timestamp
    @GetMapping("/since")
    public List<Event> getEventsAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {
        return eventService.getEventsAfter(timestamp);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Event Service is running");
    }

    // Delete event (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!eventService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}