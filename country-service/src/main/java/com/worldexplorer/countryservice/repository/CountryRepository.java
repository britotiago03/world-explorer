package com.worldexplorer.countryservice.repository;

import com.worldexplorer.countryservice.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
