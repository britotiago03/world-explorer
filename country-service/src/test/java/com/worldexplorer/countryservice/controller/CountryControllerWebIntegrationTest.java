package com.worldexplorer.countryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
@SuppressWarnings("unused")
class CountryControllerWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CountryService countryService;

    private Country norwayCountry;
    private Country swedenCountry;
    private List<Country> countryList;

    @BeforeEach
    void setUp() {
        norwayCountry = createNorwayCountry();
        norwayCountry.setId(1L);

        swedenCountry = createSwedenCountry();
        swedenCountry.setId(2L);
    }

    @Test
    void getAllCountries_ShouldReturnListOfCountries() throws Exception {
        // Given
        List<Country> countryList = Arrays.asList(norwayCountry, swedenCountry);
        when(countryService.findAll()).thenReturn(countryList);

        // When & Then
        mockMvc.perform(get("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Norway")))
                .andExpect(jsonPath("$[0].officialName", is("Kingdom of Norway")))
                .andExpect(jsonPath("$[0].isoCode", is("NO")))
                .andExpect(jsonPath("$[0].capital", is("Oslo")))
                .andExpect(jsonPath("$[0].population", is(5421241)))
                .andExpect(jsonPath("$[0].currency", is("NOK")))
                .andExpect(jsonPath("$[0].officialLanguages", hasSize(2)))
                .andExpect(jsonPath("$[0].officialLanguages[0]", is("Norwegian")))
                .andExpect(jsonPath("$[0].officialLanguages[1]", is("Sami")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Sweden")))
                .andExpect(jsonPath("$[1].isoCode", is("SE")));

        verify(countryService, times(1)).findAll();
    }

    @Test
    void getAllCountries_WhenEmpty_ShouldReturnEmptyArray() throws Exception {
        // Given
        when(countryService.findAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(empty())));

        verify(countryService, times(1)).findAll();
    }

    @Test
    void getCountryById_WhenExists_ShouldReturnCountry() throws Exception {
        // Given
        when(countryService.findById(1L)).thenReturn(Optional.of(norwayCountry));

        // When & Then
        mockMvc.perform(get("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Norway")))
                .andExpect(jsonPath("$.officialName", is("Kingdom of Norway")))
                .andExpect(jsonPath("$.isoCode", is("NO")))
                .andExpect(jsonPath("$.capital", is("Oslo")))
                .andExpect(jsonPath("$.demonym", is("Norwegian")))
                .andExpect(jsonPath("$.area", is(385207.0)))
                .andExpect(jsonPath("$.waterPercent", is(6.0)))
                .andExpect(jsonPath("$.population", is(5421241)))
                .andExpect(jsonPath("$.populationDensity", is(14.0)))
                .andExpect(jsonPath("$.callingCode", is("+47")))
                .andExpect(jsonPath("$.internetTld", is(".no")))
                .andExpect(jsonPath("$.dateFormat", is("dd.mm.yyyy")))
                .andExpect(jsonPath("$.timezone", is("CET")))
                .andExpect(jsonPath("$.summerTimezone", is("CEST")))
                .andExpect(jsonPath("$.currency", is("NOK")))
                .andExpect(jsonPath("$.nativeNames", hasSize(2)))
                .andExpect(jsonPath("$.nativeNames[0]", is("Norge")))
                .andExpect(jsonPath("$.nativeNames[1]", is("Noreg")))
                .andExpect(jsonPath("$.officialLanguages", hasSize(2)))
                .andExpect(jsonPath("$.officialLanguages", containsInAnyOrder("Norwegian", "Sami")))
                .andExpect(jsonPath("$.recognizedLanguages", hasSize(3)))
                .andExpect(jsonPath("$.recognizedLanguages", containsInAnyOrder("Norwegian", "Sami", "Kven")))
                .andExpect(jsonPath("$.flagUrl", is("https://example.com/norway-flag.png")))
                .andExpect(jsonPath("$.coatOfArmsUrl", is("https://example.com/norway-coat.png")));

        verify(countryService, times(1)).findById(1L);
    }

    @Test
    void getCountryById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(countryService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/countries/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).findById(999L);
    }

    @Test
    void getCountryById_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/countries/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).findById(anyLong());
    }

    @Test
    void createCountry_WithValidData_ShouldReturn200AndCreatedCountry() throws Exception {
        // Given
        Country newCountry = createDenmarkCountry();
        Country savedCountry = createDenmarkCountry();
        savedCountry.setId(3L);

        when(countryService.save(any(Country.class))).thenReturn(savedCountry);

        // When & Then
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCountry)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Denmark")))
                .andExpect(jsonPath("$.officialName", is("Kingdom of Denmark")))
                .andExpect(jsonPath("$.isoCode", is("DK")))
                .andExpect(jsonPath("$.capital", is("Copenhagen")))
                .andExpect(jsonPath("$.currency", is("DKK")));

        verify(countryService, times(1)).save(any(Country.class));
    }

    @Test
    void createCountry_WithMinimalData_ShouldReturn200() throws Exception {
        // Given
        Country minimalCountry = new Country();
        minimalCountry.setName("TestCountry");
        minimalCountry.setOfficialName("Test Country Official");

        Country savedCountry = new Country();
        savedCountry.setId(10L);
        savedCountry.setName("TestCountry");
        savedCountry.setOfficialName("Test Country Official");

        when(countryService.save(any(Country.class))).thenReturn(savedCountry);

        // When & Then
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("TestCountry")))
                .andExpect(jsonPath("$.officialName", is("Test Country Official")))
                .andExpect(jsonPath("$.area").doesNotExist())
                .andExpect(jsonPath("$.population").doesNotExist());

        verify(countryService, times(1)).save(any(Country.class));
    }

    @Test
    void createCountry_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then - Use malformed JSON that Jackson cannot parse
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))  // Missing closing quote and brace
                .andExpect(status().isBadRequest());

        verify(countryService, never()).save(any(Country.class));
    }

    @Test
    void createCountry_WithEmptyBody_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).save(any(Country.class));
    }

    @Test
    void updateCountry_WhenExists_ShouldReturn200AndUpdatedCountry() throws Exception {
        // Given
        Country updatedCountry = createNorwayCountry();
        updatedCountry.setId(1L);
        updatedCountry.setPopulation(5500000L);
        updatedCountry.setPopulationDensity(14.3);

        when(countryService.existsById(1L)).thenReturn(true);
        when(countryService.save(any(Country.class))).thenReturn(updatedCountry);

        // When & Then
        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Norway")))
                .andExpect(jsonPath("$.population", is(5500000)))
                .andExpect(jsonPath("$.populationDensity", is(14.3)));

        verify(countryService, times(1)).existsById(1L);
        verify(countryService, times(1)).save(any(Country.class));
    }

    @Test
    void updateCountry_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        Country updatedCountry = createNorwayCountry();
        when(countryService.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/countries/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(999L);
        verify(countryService, never()).save(any(Country.class));
    }

    @Test
    void updateCountry_WithInvalidId_ShouldReturn400() throws Exception {
        // Given
        Country updatedCountry = createNorwayCountry();

        // When & Then
        mockMvc.perform(put("/api/countries/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).existsById(anyLong());
        verify(countryService, never()).save(any(Country.class));
    }

    @Test
    void updateCountry_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then - Use malformed JSON that Jackson cannot parse
        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))  // Missing closing quote and brace
                .andExpect(status().isBadRequest());

        verify(countryService, never()).existsById(anyLong());
        verify(countryService, never()).save(any(Country.class));
    }

    @Test
    void deleteCountry_WhenExists_ShouldReturn204() throws Exception {
        // Given
        when(countryService.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(1L);
        verify(countryService, times(1)).deleteById(1L);
    }

    @Test
    void deleteCountry_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(countryService.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/countries/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

        verify(countryService, times(1)).existsById(999L);
        verify(countryService, never()).deleteById(anyLong());
    }

    @Test
    void deleteCountry_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/countries/invalid"))
                .andExpect(status().isBadRequest());

        verify(countryService, never()).existsById(anyLong());
        verify(countryService, never()).deleteById(anyLong());
    }

    @Test
    void allEndpoints_ShouldAcceptAndReturnJson() throws Exception {
        // Test that all endpoints properly handle Content-Type and Accept headers
        when(countryService.findAll()).thenReturn(List.of());
        when(countryService.findById(1L)).thenReturn(Optional.of(norwayCountry));
        when(countryService.existsById(1L)).thenReturn(true);
        when(countryService.save(any(Country.class))).thenReturn(norwayCountry);

        // GET all
        mockMvc.perform(get("/api/countries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // GET by id
        mockMvc.perform(get("/api/countries/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // POST
        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(norwayCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // PUT
        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(norwayCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void corsHeaders_ShouldBeHandledProperly() throws Exception {
        // Given
        when(countryService.findAll()).thenReturn(List.of());

        // When & Then - Test actual CORS request with Origin header
        mockMvc.perform(get("/api/countries")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
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