package partners.brito.eventservice.service;

import partners.brito.eventservice.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Objects;

@Service
@Slf4j
public class EventBroadcastService {

    private final Sinks.Many<Event> eventSink;
    private final Flux<Event> eventStream;

    public EventBroadcastService() {
        // Create a multicast sink that can handle multiple subscribers and replay the last event
        this.eventSink = Sinks.many().multicast().directBestEffort();
        this.eventStream = eventSink.asFlux().share(); // Share the stream to keep it hot

        log.info("EventBroadcastService initialized");
    }

    public void broadcastEvent(Event event) {
        log.info("Broadcasting event: type={}, id={}, entityId={}",
                event.getEventType(), event.getId(), event.getEntityId());

        // Emit the event to all subscribers
        Sinks.EmitResult result = eventSink.tryEmitNext(event);

        if (result.isFailure()) {
            log.warn("Failed to broadcast event: {}, result: {}", event.getId(), result);
        } else {
            log.debug("Successfully broadcasted event: {}", event.getId());
        }
    }

    public Flux<Event> getEventStream() {
        return eventStream
                .doOnSubscribe(subscription -> log.info("New subscriber connected to event stream"))
                .doOnCancel(() -> log.info("Subscriber disconnected from event stream"))
                .doOnError(error -> log.error("Error in event stream", error));
    }

    public Flux<Event> getEventStreamForType(String eventType) {
        return eventStream
                .filter(event -> Objects.equals(eventType, event.getEventType()))
                .doOnSubscribe(subscription -> log.info("New subscriber connected to event stream for type: {}", eventType))
                .doOnCancel(() -> log.info("Subscriber disconnected from event stream for type: {}", eventType));
    }

    public Flux<Event> getEventStreamForSource(String source) {
        return eventStream
                .filter(event -> Objects.equals(source, event.getSource()))
                .doOnSubscribe(subscription -> log.info("New subscriber connected to event stream for source: {}", source))
                .doOnCancel(() -> log.info("Subscriber disconnected from event stream for source: {}", source));
    }
}