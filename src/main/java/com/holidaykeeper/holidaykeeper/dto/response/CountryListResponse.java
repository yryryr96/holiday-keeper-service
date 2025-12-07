package com.holidaykeeper.holidaykeeper.dto.response;

import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CountryListResponse {

    private List<CountryDto> countries;

    @Builder
    private CountryListResponse(List<CountryDto> countries) {
        this.countries = countries;
    }

    public static CountryListResponse from(List<CountryDto> countryDtoList) {
        return new CountryListResponse(countryDtoList);
    }
}
