package partners.brito.eventservice.service;

import partners.brito.eventservice.model.Event;
import partners.brito.eventservice.model.EventRequest;
import partners.brito.eventservice.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(EventRequest request) {
        log.info("Creating event: type={}, source={}, entityId={}",
                request.getEventType(), request.getSource(), request.getEntityId());

        Event event = Event.builder()
                .eventType(request.getEventType())
                .source(request.getSource())
                .entityId(request.getEntityId())
                .data(request.getData())
                .version(request.getVersion() != null ? request.getVersion() : "1.0")
                .build();

        Event savedEvent = eventRepository.save(event);
        log.info("Event created with ID: {}", savedEvent.getId());

        return savedEvent;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getEventsByType(String eventType) {
        return eventRepository.findByEventTypeOrderByTimestampDesc(eventType);
    }

    public List<Event> getEventsBySource(String source) {
        return eventRepository.findBySourceOrderByTimestampDesc(source);
    }

    public List<Event> getEventsByEntityId(Long entityId) {
        return eventRepository.findByEntityIdOrderByTimestampDesc(entityId);
    }

    public List<Event> getEventsAfter(LocalDateTime timestamp) {
        return eventRepository.findByTimestampAfterOrderByTimestampDesc(timestamp);
    }

    public List<Event> getRecentEvents(int limit) {
        return eventRepository.findRecentEvents(PageRequest.of(0, limit));
    }

    public void deleteEvent(Long id) {
        log.info("Deleting event with ID: {}", id);
        eventRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }
}