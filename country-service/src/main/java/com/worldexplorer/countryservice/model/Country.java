package com.worldexplorer.countryservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "countries")
@Data
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "official_name")
    private String officialName;

    @ElementCollection
    @CollectionTable(name = "country_native_names", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "native_name")
    private List<String> nativeNames;

    @Column(name = "iso_code")
    private String isoCode;

    private String capital;
    private String demonym;
    private Double area;

    @Column(name = "water_percent")
    private Double waterPercent;

    private Long population;

    @Column(name = "population_density")
    private Double populationDensity;

    @Column(name = "calling_code")
    private String callingCode;

    @Column(name = "internet_tld")
    private String internetTld;

    @Column(name = "date_format")
    private String dateFormat;

    private String timezone;

    @Column(name = "summer_timezone")
    private String summerTimezone;

    private String currency;

    @ElementCollection
    @CollectionTable(name = "country_official_languages", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "language")
    private List<String> officialLanguages;

    @ElementCollection
    @CollectionTable(name = "country_recognized_languages", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "language")
    private List<String> recognizedLanguages;

    @Column(name = "flag_url")
    private String flagUrl;

    @Column(name = "coat_of_arms_url")
    private String coatOfArmsUrl;
}
