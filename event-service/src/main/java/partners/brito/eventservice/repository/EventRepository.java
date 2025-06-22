package partners.brito.eventservice.repository;

import partners.brito.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by type
    List<Event> findByEventTypeOrderByTimestampDesc(String eventType);

    // Find events by source
    List<Event> findBySourceOrderByTimestampDesc(String source);

    // Find events by entity ID
    List<Event> findByEntityIdOrderByTimestampDesc(Long entityId);

    // Find events after a specific timestamp
    List<Event> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);

    // Find events by type and source
    List<Event> findByEventTypeAndSourceOrderByTimestampDesc(String eventType, String source);

    // Custom query to find recent events (last N events)
    @Query("SELECT e FROM Event e ORDER BY e.timestamp DESC")
    List<Event> findRecentEvents(org.springframework.data.domain.Pageable pageable);

    // Find events within a time range
    @Query("SELECT e FROM Event e WHERE e.timestamp BETWEEN :startTime AND :endTime ORDER BY e.timestamp DESC")
    List<Event> findEventsBetween(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);
}