package com.worldexplorer.countryservice.repository;

import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CountryRepositoryPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    private Country norwayCountry;

    @BeforeEach
    void setUp() {
        countryRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        norwayCountry = CountryTestDataFactory.createNorway();
    }

    @Test
    void save_ShouldPersistCountryWithAllFields() {
        // When
        Country savedCountry = countryRepository.save(norwayCountry);
        entityManager.flush();

        // Then
        assertThat(savedCountry).isNotNull();
        assertThat(savedCountry.getId()).isNotNull();
        assertThat(savedCountry.getName()).isEqualTo("Norway");
        assertThat(savedCountry.getOfficialName()).isEqualTo("Kingdom of Norway");
        assertThat(savedCountry.getIsoCode()).isEqualTo("NO");
        assertThat(savedCountry.getCapital()).isEqualTo("Oslo");
        assertThat(savedCountry.getDemonym()).isEqualTo("Norwegian");
        assertThat(savedCountry.getArea()).isEqualTo(385207.0);
        assertThat(savedCountry.getWaterPercent()).isEqualTo(6.0);
        assertThat(savedCountry.getPopulation()).isEqualTo(5421241L);
        assertThat(savedCountry.getPopulationDensity()).isEqualTo(14.0);
        assertThat(savedCountry.getCallingCode()).isEqualTo("+47");
        assertThat(savedCountry.getInternetTld()).isEqualTo(".no");
        assertThat(savedCountry.getDateFormat()).isEqualTo("dd.mm.yyyy");
        assertThat(savedCountry.getTimezone()).isEqualTo("CET");
        assertThat(savedCountry.getSummerTimezone()).isEqualTo("CEST");
        assertThat(savedCountry.getCurrency()).isEqualTo("NOK");
        assertThat(savedCountry.getOfficialLanguages()).containsExactly("Norwegian", "Sami");
        assertThat(savedCountry.getRecognizedLanguages()).containsExactlyInAnyOrder("Norwegian", "Sami", "Kven");
        assertThat(savedCountry.getFlagUrl()).isEqualTo("https://example.com/norway-flag.png");
        assertThat(savedCountry.getCoatOfArmsUrl()).isEqualTo("https://example.com/norway-coat.png");
    }

    @Test
    void save_WhenUpdatingExistingCountry_ShouldPersistChanges() {
        // Given
        Country savedCountry = entityManager.persistAndFlush(norwayCountry);
        entityManager.clear();

        // When - update population
        savedCountry.setPopulation(5500000L);
        savedCountry.setPopulationDensity(14.3);
        countryRepository.save(savedCountry);
        entityManager.flush();
        entityManager.clear();

        // Then
        Country reloadedCountry = entityManager.find(Country.class, savedCountry.getId());
        assertThat(reloadedCountry.getPopulation()).isEqualTo(5500000L);
        assertThat(reloadedCountry.getPopulationDensity()).isEqualTo(14.3);
        assertThat(reloadedCountry.getName()).isEqualTo("Norway"); // Other fields unchanged
    }

    @Test
    void save_WithNullOptionalFields_ShouldPersistSuccessfully() {
        // Given
        Country minimalCountry = CountryTestDataFactory.createMinimal("TestCountry", "Test Country Official");

        // When
        Country savedCountry = countryRepository.save(minimalCountry);
        entityManager.flush();

        // Then
        assertThat(savedCountry.getId()).isNotNull();
        assertThat(savedCountry.getName()).isEqualTo("TestCountry");
        assertThat(savedCountry.getOfficialName()).isEqualTo("Test Country Official");
        assertThat(savedCountry.getArea()).isNull();
        assertThat(savedCountry.getPopulation()).isNull();
        assertThat(savedCountry.getOfficialLanguages()).isNull();
    }

    @Test
    void idGeneration_ShouldUseIdentityStrategy() {
        // Given
        Country country1 = new Country();
        country1.setName("Country1");

        Country country2 = new Country();
        country2.setName("Country2");

        // When
        Country saved1 = countryRepository.save(country1);
        Country saved2 = countryRepository.save(country2);
        entityManager.flush();

        // Then
        assertThat(saved1.getId()).isNotNull();
        assertThat(saved2.getId()).isNotNull();
        assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
    }
}