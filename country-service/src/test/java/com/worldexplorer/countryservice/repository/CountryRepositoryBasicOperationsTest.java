package com.worldexplorer.countryservice.repository;

import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.testdata.CountryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CountryRepositoryBasicOperationsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    private Country norwayCountry;
    private Country swedenCountry;
    private Country denmarkCountry;

    @BeforeEach
    void setUp() {
        countryRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        norwayCountry = CountryTestDataFactory.createNorway();
        swedenCountry = CountryTestDataFactory.createSwedenWithId(null);
        denmarkCountry = CountryTestDataFactory.createDenmark();
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
}