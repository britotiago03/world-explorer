package com.worldexplorer.countryservice.api;

import com.worldexplorer.countryservice.business.CountryService;
import com.worldexplorer.countryservice.domain.Country;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Country> getAllCountries() {
        return service.findAll();
    }
}
