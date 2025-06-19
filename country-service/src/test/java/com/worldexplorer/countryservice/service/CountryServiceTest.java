package com.worldexplorer.countryservice.service;

import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setName("Norway");
        testCountry.setOfficialName("Kingdom of Norway");
        testCountry.setIsoCode("NO");
        testCountry.setCapital("Oslo");
        testCountry.setDemonym("Norwegian");
        testCountry.setArea(385207.0);
        testCountry.setWaterPercent(6.0);
        testCountry.setPopulation(5421241L);
        testCountry.setPopulationDensity(14.0);
        testCountry.setCallingCode("+47");
        testCountry.setInternetTld(".no");
        testCountry.setDateFormat("dd.mm.yyyy");
        testCountry.setTimezone("CET");
        testCountry.setSummerTimezone("CEST");
        testCountry.setCurrency("NOK");
        testCountry.setOfficialLanguages(Arrays.asList("Norwegian", "Sami"));
        testCountry.setRecognizedLanguages(Arrays.asList("Norwegian", "Sami", "Kven"));
        testCountry.setFlagUrl("https://example.com/norway-flag.png");
        testCountry.setCoatOfArmsUrl("https://example.com/norway-coat.png");
    }

    @Test
    void findAll_ShouldReturnAllCountries() {
        // Given
        Country country2 = new Country();
        country2.setId(2L);
        country2.setName("Sweden");
        country2.setOfficialName("Kingdom of Sweden");

        List<Country> expectedCountries = Arrays.asList(testCountry, country2);
        when(countryRepository.findAll()).thenReturn(expectedCountries);

        // When
        List<Country> actualCountries = countryService.findAll();

        // Then
        assertThat(actualCountries).isNotNull();
        assertThat(actualCountries).hasSize(2);
        assertThat(actualCountries).containsExactlyElementsOf(expectedCountries);
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoCountries_ShouldReturnEmptyList() {
        // Given
        when(countryRepository.findAll()).thenReturn(List.of());

        // When
        List<Country> actualCountries = countryService.findAll();

        // Then
        assertThat(actualCountries).isNotNull();
        assertThat(actualCountries).isEmpty();
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenCountryExists_ShouldReturnCountry() {
        // Given
        Long countryId = 1L;
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(testCountry));

        // When
        Optional<Country> result = countryService.findById(countryId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testCountry);
        assertThat(result.get().getName()).isEqualTo("Norway");
        verify(countryRepository, times(1)).findById(countryId);
    }

    @Test
    void findById_WhenCountryDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        Long nonExistentId = 999L;
        when(countryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Country> result = countryService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(countryRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void save_WhenCreatingNewCountry_ShouldReturnSavedCountry() {
        // Given
        Country newCountry = new Country();
        newCountry.setName("Denmark");
        newCountry.setOfficialName("Kingdom of Denmark");

        Country savedCountry = new Country();
        savedCountry.setId(3L);
        savedCountry.setName("Denmark");
        savedCountry.setOfficialName("Kingdom of Denmark");

        when(countryRepository.save(newCountry)).thenReturn(savedCountry);

        // When
        Country result = countryService.save(newCountry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Denmark");
        assertThat(result.getOfficialName()).isEqualTo("Kingdom of Denmark");
        verify(countryRepository, times(1)).save(newCountry);
    }

    @Test
    void save_WhenUpdatingExistingCountry_ShouldReturnUpdatedCountry() {
        // Given
        testCountry.setPopulation(5500000L); // Updated population
        when(countryRepository.save(testCountry)).thenReturn(testCountry);

        // When
        Country result = countryService.save(testCountry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPopulation()).isEqualTo(5500000L);
        verify(countryRepository, times(1)).save(testCountry);
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        Long countryId = 1L;

        // When
        countryService.deleteById(countryId);

        // Then
        verify(countryRepository, times(1)).deleteById(countryId);
    }

    @Test
    void existsById_WhenCountryExists_ShouldReturnTrue() {
        // Given
        Long countryId = 1L;
        when(countryRepository.existsById(countryId)).thenReturn(true);

        // When
        boolean result = countryService.existsById(countryId);

        // Then
        assertThat(result).isTrue();
        verify(countryRepository, times(1)).existsById(countryId);
    }

    @Test
    void existsById_WhenCountryDoesNotExist_ShouldReturnFalse() {
        // Given
        Long nonExistentId = 999L;
        when(countryRepository.existsById(nonExistentId)).thenReturn(false);

        // When
        boolean result = countryService.existsById(nonExistentId);

        // Then
        assertThat(result).isFalse();
        verify(countryRepository, times(1)).existsById(nonExistentId);
    }

    @Test
    void constructor_ShouldInjectRepository() {
        // Given/When
        CountryService service = new CountryService(countryRepository);

        // Then
        assertThat(service).isNotNull();
        // Verify that the repository is properly injected by calling a method
        service.findAll();
        verify(countryRepository, times(1)).findAll();
    }
}