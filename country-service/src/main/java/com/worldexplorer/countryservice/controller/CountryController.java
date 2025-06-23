package com.worldexplorer.countryservice.controller;

import com.worldexplorer.countryservice.model.Country;
import com.worldexplorer.countryservice.service.CountryService;
import com.worldexplorer.countryservice.service.EventPublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin(origins = "*")
public class CountryController {

    private final CountryService service;
    private final EventPublisherService eventPublisher;

    public CountryController(CountryService service, EventPublisherService eventPublisher) {
        this.service = service;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public List<Country> getAllCountries() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        Country savedCountry = service.save(country);

        // Publish country created event
        eventPublisher.publishCountryCreated(savedCountry);

        return ResponseEntity.ok(savedCountry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Long id, @RequestBody Country updated) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Country existingCountry = service.findById(id).orElse(null);
        updated.setId(id);
        Country savedCountry = service.save(updated);

        // Publish country updated event
        eventPublisher.publishCountryUpdated(savedCountry, existingCountry);

        return ResponseEntity.ok(savedCountry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Country countryToDelete = service.findById(id).orElse(null);
        service.deleteById(id);

        // Publish country deleted event
        if (countryToDelete != null) {
            eventPublisher.publishCountryDeleted(countryToDelete);
        }

        return ResponseEntity.noContent().build();
    }
}