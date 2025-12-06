package com.holidaykeeper.holidaykeeper.dto.response;

import com.holidaykeeper.holidaykeeper.domain.Holiday;
import com.holidaykeeper.holidaykeeper.domain.HolidayType;
import com.holidaykeeper.holidaykeeper.domain.HolidayTypeMap;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class HolidayResponse {

    private LocalDate date;
    private String localName;
    private String name;
    private String countryCode;
    private Boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;

    @Builder
    private HolidayResponse(LocalDate date, String localName, String name, String countryCode,
                           Boolean global, List<String> counties, Integer launchYear, List<String> types) {
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.countryCode = countryCode;
        this.global = global;
        this.counties = counties;
        this.launchYear = launchYear;
        this.types = types;
    }

    public static HolidayResponse from(Holiday holiday) {

        List<String> counties = holiday.getCounties().stream()
                .map(c -> c.getCounty().getName())
                .toList();

        List<String> types = holiday.getTypes().stream()
                .map(HolidayTypeMap::getType)
                .map(HolidayType::name)
                .toList();

        return HolidayResponse.builder()
                .date(holiday.getDate())
                .localName(holiday.getLocalName())
                .name(holiday.getName())
                .countryCode(holiday.getCountry().getCode())
                .global(holiday.isGlobal())
                .counties(counties)
                .launchYear(holiday.getLaunchYear())
                .types(types)
                .build();
    }
}
