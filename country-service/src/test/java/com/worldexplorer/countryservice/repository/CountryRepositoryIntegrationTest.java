package com.worldexplorer.countryservice.repository;

import com.worldexplorer.countryservice.model.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CountryRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    private Country norwayCountry;
    private Country swedenCountry;
    private Country denmarkCountry;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        countryRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create test countries
        norwayCountry = createNorwayCountry();
        swedenCountry = createSwedenCountry();
        denmarkCountry = createDenmarkCountry();
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
    void save_ShouldPersistElementCollections() {
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
    void findById_WhenCountryExists_ShouldReturnCountry() {
        // Given
        Country savedCountry = entityManager.persistAndFlush(norwayCountry);
        entityManager.clear();

        // When
        Optional<Country> result = countryRepository.findById(savedCountry.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Norway");
        assertThat(result.get().getIsoCode()).isEqualTo("NO");
    }

    @Test
    void findById_WhenCountryDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Country> result = countryRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllCountries() {
        // Given
        entityManager.persistAndFlush(norwayCountry);
        entityManager.persistAndFlush(swedenCountry);
        entityManager.persistAndFlush(denmarkCountry);
        entityManager.clear();

        // When
        List<Country> countries = countryRepository.findAll();

        // Then
        assertThat(countries).hasSize(3);
        assertThat(countries).extracting(Country::getName)
                .containsExactlyInAnyOrder("Norway", "Sweden", "Denmark");
    }

    @Test
    void deleteById_ShouldRemoveCountryFromDatabase() {
        // Given
        Country savedCountry = entityManager.persistAndFlush(norwayCountry);
        Long countryId = savedCountry.getId();
        entityManager.clear();

        // Verify it exists
        assertThat(countryRepository.findById(countryId)).isPresent();

        // When
        countryRepository.deleteById(countryId);
        entityManager.flush();

        // Then
        assertThat(countryRepository.findById(countryId)).isEmpty();
    }

    @Test
    void existsById_WhenCountryExists_ShouldReturnTrue() {
        // Given
        Country savedCountry = entityManager.persistAndFlush(norwayCountry);
        entityManager.clear();

        // When
        boolean exists = countryRepository.existsById(savedCountry.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WhenCountryDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = countryRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void count_ShouldReturnCorrectNumberOfCountries() {
        // Given
        entityManager.persistAndFlush(norwayCountry);
        entityManager.persistAndFlush(swedenCountry);
        entityManager.clear();

        // When
        long count = countryRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
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
        Country minimalCountry = new Country();
        minimalCountry.setName("TestCountry");
        minimalCountry.setOfficialName("Test Country Official");

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

    // Helper methods to create test data
    private Country createNorwayCountry() {
        Country country = new Country();
        country.setName("Norway");
        country.setOfficialName("Kingdom of Norway");
        country.setNativeNames(Arrays.asList("Norge", "Noreg"));
        country.setIsoCode("NO");
        country.setCapital("Oslo");
        country.setDemonym("Norwegian");
        country.setArea(385207.0);
        country.setWaterPercent(6.0);
        country.setPopulation(5421241L);
        country.setPopulationDensity(14.0);
        country.setCallingCode("+47");
        country.setInternetTld(".no");
        country.setDateFormat("dd.mm.yyyy");
        country.setTimezone("CET");
        country.setSummerTimezone("CEST");
        country.setCurrency("NOK");
        country.setOfficialLanguages(Arrays.asList("Norwegian", "Sami"));
        country.setRecognizedLanguages(Arrays.asList("Norwegian", "Sami", "Kven"));
        country.setFlagUrl("https://example.com/norway-flag.png");
        country.setCoatOfArmsUrl("https://example.com/norway-coat.png");
        return country;
    }

    private Country createSwedenCountry() {
        Country country = new Country();
        country.setName("Sweden");
        country.setOfficialName("Kingdom of Sweden");
        country.setNativeNames(List.of("Sverige"));
        country.setIsoCode("SE");
        country.setCapital("Stockholm");
        country.setDemonym("Swedish");
        country.setArea(450295.0);
        country.setWaterPercent(8.7);
        country.setPopulation(10353442L);
        country.setPopulationDensity(23.0);
        country.setCallingCode("+46");
        country.setInternetTld(".se");
        country.setDateFormat("yyyy-mm-dd");
        country.setTimezone("CET");
        country.setSummerTimezone("CEST");
        country.setCurrency("SEK");
        country.setOfficialLanguages(List.of("Swedish"));
        country.setRecognizedLanguages(Arrays.asList("Swedish", "Finnish", "Meänkieli", "Sami", "Romani"));
        country.setFlagUrl("https://example.com/sweden-flag.png");
        country.setCoatOfArmsUrl("https://example.com/sweden-coat.png");
        return country;
    }

    private Country createDenmarkCountry() {
        Country country = new Country();
        country.setName("Denmark");
        country.setOfficialName("Kingdom of Denmark");
        country.setNativeNames(List.of("Danmark"));
        country.setIsoCode("DK");
        country.setCapital("Copenhagen");
        country.setDemonym("Danish");
        country.setArea(43094.0);
        country.setWaterPercent(1.6);
        country.setPopulation(5822763L);
        country.setPopulationDensity(135.0);
        country.setCallingCode("+45");
        country.setInternetTld(".dk");
        country.setDateFormat("dd-mm-yyyy");
        country.setTimezone("CET");
        country.setSummerTimezone("CEST");
        country.setCurrency("DKK");
        country.setOfficialLanguages(List.of("Danish"));
        country.setRecognizedLanguages(Arrays.asList("Danish", "Faroese", "Greenlandic", "German"));
        country.setFlagUrl("https://example.com/denmark-flag.png");
        country.setCoatOfArmsUrl("https://example.com/denmark-coat.png");
        return country;
    }
}