package com.worldexplorer.countryservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class CountryServiceConfigTest {

    private final CountryServiceConfig config = new CountryServiceConfig();

    @Test
    void restTemplate_ShouldCreateValidInstance() {
        // When
        RestTemplate restTemplate = config.restTemplate();

        // Then
        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate).isInstanceOf(RestTemplate.class);
    }

    @Test
    void objectMapper_ShouldCreateValidInstance() {
        // When
        ObjectMapper objectMapper = config.objectMapper();

        // Then
        assertThat(objectMapper).isNotNull();
        assertThat(objectMapper).isInstanceOf(ObjectMapper.class);
    }

    @Test
    void objectMapper_ShouldBeConfiguredProperly() throws Exception {
        // Given
        ObjectMapper objectMapper = config.objectMapper();
        String jsonString = "{\"name\":\"test\",\"value\":123}";

        // When
        Object result = objectMapper.readValue(jsonString, Object.class);

        // Then
        assertThat(result).isNotNull();
        assertThat(objectMapper.writeValueAsString(result)).isNotNull();
    }

    @Test
    void restTemplate_ShouldBeReusable() {
        // Given
        RestTemplate restTemplate1 = config.restTemplate();
        RestTemplate restTemplate2 = config.restTemplate();

        // Then
        assertThat(restTemplate1).isNotNull();
        assertThat(restTemplate2).isNotNull();
        // Note: These are different instances since @Bean creates new instances each time
        // unless @Scope is specified as singleton (which is default in Spring context)
        assertThat(restTemplate1).isNotSameAs(restTemplate2);
    }

    @Test
    void objectMapper_ShouldBeReusable() {
        // Given
        ObjectMapper objectMapper1 = config.objectMapper();
        ObjectMapper objectMapper2 = config.objectMapper();

        // Then
        assertThat(objectMapper1).isNotNull();
        assertThat(objectMapper2).isNotNull();
        // Note: These are different instances since @Bean creates new instances each time
        // unless @Scope is specified as singleton (which is default in Spring context)
        assertThat(objectMapper1).isNotSameAs(objectMapper2);
    }
}