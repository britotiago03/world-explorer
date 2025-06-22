package partners.brito.eventservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {

    private Validator validator;
    private ValidatorFactory factory;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    void validEventRequest_ShouldPassValidation() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void eventRequestWithNullEventType_ShouldFailValidation() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType(null)
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Event type is required", violation.getMessage());
        assertEquals("eventType", violation.getPropertyPath().toString());
    }

    @Test
    void eventRequestWithBlankEventType_ShouldFailValidation() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType("   ")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Event type is required", violation.getMessage());
    }

    @Test
    void eventRequestWithTooLongEventType_ShouldFailValidation() {
        // Given
        String longEventType = "A".repeat(51); // 51 characters
        EventRequest request = EventRequest.builder()
                .eventType(longEventType)
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Event type must not exceed 50 characters", violation.getMessage());
    }

    @Test
    void eventRequestWithNullSource_ShouldFailValidation() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source(null)
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Source is required", violation.getMessage());
        assertEquals("source", violation.getPropertyPath().toString());
    }

    @Test
    void eventRequestWithBlankSource_ShouldFailValidation() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Source is required", violation.getMessage());
    }

    @Test
    void eventRequestWithTooLongSource_ShouldFailValidation() {
        // Given
        String longSource = "B".repeat(51); // 51 characters
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source(longSource)
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Source must not exceed 50 characters", violation.getMessage());
    }

    @Test
    void eventRequestWithTooLongVersion_ShouldFailValidation() {
        // Given
        String longVersion = "1".repeat(11); // 11 characters
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version(longVersion)
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<EventRequest> violation = violations.iterator().next();
        assertEquals("Version must not exceed 10 characters", violation.getMessage());
    }

    @Test
    void eventRequestWithNullOptionalFields_ShouldPassValidation() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(null)
                .data(null)
                .version(null)
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void eventRequestWithMultipleViolations_ShouldReturnAllViolations() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType(null)
                .source("")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("V".repeat(11))
                .build();

        // When
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);

        // Then
        assertEquals(3, violations.size());
    }

    @Test
    void builder_ShouldCreateEventRequestWithAllFields() {
        // When
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_UPDATED")
                .source("country-service")
                .entityId(456L)
                .data("{\"name\":\"Spain\"}")
                .version("1.5")
                .build();

        // Then
        assertEquals("COUNTRY_UPDATED", request.getEventType());
        assertEquals("country-service", request.getSource());
        assertEquals(456L, request.getEntityId());
        assertEquals("{\"name\":\"Spain\"}", request.getData());
        assertEquals("1.5", request.getVersion());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyEventRequest() {
        // When
        EventRequest request = new EventRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getEventType());
        assertNull(request.getSource());
        assertNull(request.getEntityId());
        assertNull(request.getData());
        assertNull(request.getVersion());
    }

    @Test
    void allArgsConstructor_ShouldCreateEventRequestWithAllFields() {
        // When
        EventRequest request = new EventRequest("COUNTRY_DELETED", "country-service",
                789L, "{\"id\":789}", "1.0");

        // Then
        assertEquals("COUNTRY_DELETED", request.getEventType());
        assertEquals("country-service", request.getSource());
        assertEquals(789L, request.getEntityId());
        assertEquals("{\"id\":789}", request.getData());
        assertEquals("1.0", request.getVersion());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        EventRequest request = new EventRequest();

        // When
        request.setEventType("USER_CREATED");
        request.setSource("user-service");
        request.setEntityId(999L);
        request.setData("{\"username\":\"john\"}");
        request.setVersion("2.1");

        // Then
        assertEquals("USER_CREATED", request.getEventType());
        assertEquals("user-service", request.getSource());
        assertEquals(999L, request.getEntityId());
        assertEquals("{\"username\":\"john\"}", request.getData());
        assertEquals("2.1", request.getVersion());
    }

    @Test
    void equals_ShouldWorkCorrectlyForSameRequests() {
        // Given
        EventRequest request1 = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        EventRequest request2 = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // Then
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalseForDifferentRequests() {
        // Given
        EventRequest request1 = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .build();

        EventRequest request2 = EventRequest.builder()
                .eventType("COUNTRY_UPDATED")
                .source("country-service")
                .build();

        // Then
        assertNotEquals(request1, request2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Given
        EventRequest request = EventRequest.builder()
                .eventType("COUNTRY_CREATED")
                .source("country-service")
                .entityId(123L)
                .data("{\"name\":\"Portugal\"}")
                .version("1.0")
                .build();

        // When
        String toString = request.toString();

        // Then
        assertTrue(toString.contains("eventType=COUNTRY_CREATED"));
        assertTrue(toString.contains("source=country-service"));
        assertTrue(toString.contains("entityId=123"));
        assertTrue(toString.contains("data={\"name\":\"Portugal\"}"));
        assertTrue(toString.contains("version=1.0"));
    }
}