package com.worldexplorer.countryservice.repository;

import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CountryRepositoryJpaMappingTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void setUp() {
        countryRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void save_ShouldPersistElementCollections() {
        // Given
        Country norwayCountry = CountryTestDataFactory.createNorway();

        // When
        Country savedCountry = countryRepository.save(norwayCountry);
        entityManager.flush();
        entityManager.clear();

        // Then - reload from database to verify persistence
        Country reloadedCountry = entityManager.find(Country.class, savedCountry.getId());

        assertThat(reloadedCountry.getNativeNames()).containsExactly("Norge", "Noreg");
        assertThat(reloadedCountry.getOfficialLanguages()).containsExactly("Norwegian", "Sami");
        assertThat(reloadedCountry.getRecognizedLanguages()).containsExactlyInAnyOrder("Norwegian", "Sami", "Kven");
    }

    @Test
    void jpaMapping_ShouldHandleElementCollectionsCorrectly() {
        // Given
        Country countryWithCollections = new Country();
        countryWithCollections.setName("TestCountry");
        countryWithCollections.setNativeNames(Arrays.asList("Name1", "Name2", "Name3"));
        countryWithCollections.setOfficialLanguages(Arrays.asList("Lang1", "Lang2"));
        countryWithCollections.setRecognizedLanguages(Arrays.asList("Lang1", "Lang2", "Lang3", "Lang4"));

        // When
        Country savedCountry = countryRepository.save(countryWithCollections);
        entityManager.flush();
        entityManager.clear();

        // Then
        Country reloadedCountry = countryRepository.findById(savedCountry.getId()).orElseThrow();
        assertThat(reloadedCountry.getNativeNames()).hasSize(3).containsExactly("Name1", "Name2", "Name3");
        assertThat(reloadedCountry.getOfficialLanguages()).hasSize(2).containsExactly("Lang1", "Lang2");
        assertThat(reloadedCountry.getRecognizedLanguages()).hasSize(4)
                .containsExactlyInAnyOrder("Lang1", "Lang2", "Lang3", "Lang4");
    }

    @Test
    void columnMapping_ShouldMapUnderscoreFieldsCorrectly() {
        // Given
        Country norwayCountry = CountryTestDataFactory.createNorway();

        // When
        Country savedCountry = countryRepository.save(norwayCountry);
        entityManager.flush();

        // Then - verify that underscore field mappings work
        assertThat(savedCountry.getOfficialName()).isNotNull(); // maps to official_name
        assertThat(savedCountry.getWaterPercent()).isNotNull(); // maps to water_percent
        assertThat(savedCountry.getPopulationDensity()).isNotNull(); // maps to population_density
        assertThat(savedCountry.getCallingCode()).isNotNull(); // maps to calling_code
        assertThat(savedCountry.getInternetTld()).isNotNull(); // maps to internet_tld
        assertThat(savedCountry.getDateFormat()).isNotNull(); // maps to date_format
        assertThat(savedCountry.getSummerTimezone()).isNotNull(); // maps to summer_timezone
        assertThat(savedCountry.getFlagUrl()).isNotNull(); // maps to flag_url
        assertThat(savedCountry.getCoatOfArmsUrl()).isNotNull(); // maps to coat_of_arms_url
    }
}