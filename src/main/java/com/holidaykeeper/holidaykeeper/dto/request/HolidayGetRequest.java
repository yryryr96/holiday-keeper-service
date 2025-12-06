package com.holidaykeeper.holidaykeeper.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class HolidayGetRequest {

    private Integer year;
    private String countryCode;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer page;
    private Integer size;
}
