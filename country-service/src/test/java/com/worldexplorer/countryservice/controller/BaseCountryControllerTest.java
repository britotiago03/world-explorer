package com.worldexplorer.countryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.service.CountryService;
import com.worldexplorer.countryservice.service.EventPublisherService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.worldexplorer.countryservice.testdata.CountryTestDataFactory.*;

/**
 * Base class for Country Controller tests.
 * Provides common setup and dependencies for all controller test classes.
 */
@WebMvcTest(CountryController.class)
@SuppressWarnings("unused")
public abstract class BaseCountryControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected CountryService countryService;

    @MockitoBean
    protected EventPublisherService eventPublisherService;

    // Common test data
    protected com.worldexplorer.countryservice.model.Country norwayCountry;
    protected com.worldexplorer.countryservice.model.Country swedenCountry;
    protected com.worldexplorer.countryservice.model.Country denmarkCountry;

    @BeforeEach
    void setUpBaseData() {
        norwayCountry = createNorwayWithId(1L);
        swedenCountry = createSwedenWithId(2L);
        denmarkCountry = createDenmarkWithId(3L);
    }
}