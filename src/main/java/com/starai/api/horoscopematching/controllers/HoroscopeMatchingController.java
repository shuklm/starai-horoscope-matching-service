package com.starai.api.horoscopematching.controllers;

import com.starai.api.horoscopematching.entities.HoroscopeMatching;
import com.starai.api.horoscopematching.services.HoroscopeMatchingService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horoscopematching")
@Slf4j
public class HoroscopeMatchingController {

    private final HoroscopeMatchingService horoscopeMatchingService;

    @Autowired
    public HoroscopeMatchingController(HoroscopeMatchingService horoscopeMatchingService) {
        this.horoscopeMatchingService = horoscopeMatchingService;
    }

    @GetMapping
    public List<HoroscopeMatching> getAllHoroscopeMatchings() {
        return horoscopeMatchingService.findAllHoroscopeMatchings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HoroscopeMatching> getHoroscopeMatchingById(@PathVariable Long id) {
        return horoscopeMatchingService
                .findHoroscopeMatchingById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HoroscopeMatching createHoroscopeMatching(@RequestBody @Validated HoroscopeMatching horoscopeMatching) {
        return horoscopeMatchingService.saveHoroscopeMatching(horoscopeMatching);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HoroscopeMatching> updateHoroscopeMatching(
            @PathVariable Long id, @RequestBody HoroscopeMatching horoscopeMatching) {
        return horoscopeMatchingService
                .findHoroscopeMatchingById(id)
                .map(
                        horoscopeMatchingObj -> {
                            horoscopeMatching.setId(id);
                            return ResponseEntity.ok(horoscopeMatchingService.saveHoroscopeMatching(horoscopeMatching));
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HoroscopeMatching> deleteHoroscopeMatching(@PathVariable Long id) {
        return horoscopeMatchingService
                .findHoroscopeMatchingById(id)
                .map(
                        horoscopeMatching -> {
                            horoscopeMatchingService.deleteHoroscopeMatchingById(id);
                            return ResponseEntity.ok(horoscopeMatching);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
