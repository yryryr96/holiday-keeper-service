package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.dto.response.CountryListResponse;

import java.util.List;

public interface CountryService {

    List<Country> saveAll(List<CountryDto> countryDtoList);
    Country findByCode(String countryCode);
    CountryListResponse getCountries();
}
