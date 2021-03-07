package com.starai.api.horoscopematching.repositories;

import com.starai.api.horoscopematching.entities.HoroscopeMatching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoroscopeMatchingRepository extends JpaRepository<HoroscopeMatching, Long> {}
