package com.holidaykeeper.holidaykeeper.dto;

import com.holidaykeeper.holidaykeeper.domain.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {

    private String countryCode;
    private String name;

    public Country toEntity() {
        return Country.builder()
                .code(countryCode)
                .name(name)
                .build();
    }
}
