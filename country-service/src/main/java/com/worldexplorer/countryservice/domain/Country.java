package com.worldexplorer.countryservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "iso_code")
    private String isoCode;

    private String capital;

    private Long population;

    private Double area;

    // Getters and Setters (or use Lombok's @Getter/@Setter/@Data)
}
