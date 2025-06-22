package partners.brito.eventservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "version", length = 10)
    private String version;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (version == null) {
            version = "1.0";
        }
    }
}