package com.holidaykeeper.holidaykeeper.repository;

import com.holidaykeeper.holidaykeeper.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByCode(String name);
}
