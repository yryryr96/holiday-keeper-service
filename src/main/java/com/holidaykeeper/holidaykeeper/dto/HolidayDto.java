package com.holidaykeeper.holidaykeeper.dto;

import com.holidaykeeper.holidaykeeper.domain.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDto {

    private LocalDate date;
    private String localName;
    private String name;
    private String countryCode;
    private Boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;

    public List<HolidayType> convertToHolidayTypes() {
        if (types == null || types.isEmpty()) {
            return List.of();
        }

        return types.stream()
                .map(String::toUpperCase)
                .map(HolidayType::valueOf)
                .toList();
    }
}
