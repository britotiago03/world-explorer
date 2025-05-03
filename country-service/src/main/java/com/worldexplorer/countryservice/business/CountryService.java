package com.worldexplorer.countryservice.business;

import com.worldexplorer.countryservice.domain.Country;
import com.worldexplorer.countryservice.persistence.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    public List<Country> findAll() {
        return repository.findAll();
    }
}
