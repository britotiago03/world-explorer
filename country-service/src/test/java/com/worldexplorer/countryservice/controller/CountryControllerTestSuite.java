package com.worldexplorer.countryservice.controller;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite that runs all Country Controller tests.
 * This provides a convenient way to run all related tests together.
 */
@Suite
@SelectClasses({
        GetCountriesControllerTest.class,
        CreateCountryControllerTest.class,
        UpdateCountryControllerTest.class,
        DeleteCountryControllerTest.class,
        CountryControllerIntegrationTest.class
})
public class CountryControllerTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}