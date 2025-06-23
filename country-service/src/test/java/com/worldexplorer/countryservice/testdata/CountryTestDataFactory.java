package com.worldexplorer.countryservice.testdata;

import com.worldexplorer.countryservice.model.Country;

import java.util.Arrays;
import java.util.List;

/**
 * Factory class for creating test Country objects.
 * Centralizes test data creation to ensure consistency across tests.
 */
public class CountryTestDataFactory {

    public static Country createNorway() {
        return createNorwayWithId(null);
    }

    public static Country createNorwayWithId(Long id) {
        Country country = new Country();
        country.setId(id);
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

    public static Country createSwedenWithId(Long id) {
        Country country = new Country();
        country.setId(id);
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

    public static Country createDenmark() {
        return createDenmarkWithId(null);
    }

    public static Country createDenmarkWithId(Long id) {
        Country country = new Country();
        country.setId(id);
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

    public static Country createMinimal(String name, String officialName) {
        Country country = new Country();
        country.setName(name);
        country.setOfficialName(officialName);
        return country;
    }

    public static Country createMinimalWithId(Long id, String name, String officialName) {
        Country country = createMinimal(name, officialName);
        country.setId(id);
        return country;
    }
}