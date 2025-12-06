package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NagerCountryService implements CountryService {

    private final CountryRepository countryRepository;

    @Transactional
    @Override
    public List<Country> saveAll(List<CountryDto> countryDtoList) {

        List<Country> countries = countryDtoList.stream()
                .map(CountryDto::toEntity)
                .toList();

        return countryRepository.saveAll(countries);
    }

    @Transactional(readOnly = true)
    @Override
    public Country findByCode(String countryCode) {
        return countryRepository.findByCode(countryCode);
    }
}
