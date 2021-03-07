package com.starai.api.horoscopematching.services;

import com.starai.api.horoscopematching.entities.HoroscopeMatching;
import com.starai.api.horoscopematching.repositories.HoroscopeMatchingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HoroscopeMatchingService {

    private final HoroscopeMatchingRepository horoscopeMatchingRepository;

    @Autowired
    public HoroscopeMatchingService(HoroscopeMatchingRepository horoscopeMatchingRepository) {
        this.horoscopeMatchingRepository = horoscopeMatchingRepository;
    }

    public List<HoroscopeMatching> findAllHoroscopeMatchings() {
        return horoscopeMatchingRepository.findAll();
    }

    public Optional<HoroscopeMatching> findHoroscopeMatchingById(Long id) {
        return horoscopeMatchingRepository.findById(id);
    }

    public HoroscopeMatching saveHoroscopeMatching(HoroscopeMatching horoscopeMatching) {
        return horoscopeMatchingRepository.save(horoscopeMatching);
    }

    public void deleteHoroscopeMatchingById(Long id) {
        horoscopeMatchingRepository.deleteById(id);
    }
}
