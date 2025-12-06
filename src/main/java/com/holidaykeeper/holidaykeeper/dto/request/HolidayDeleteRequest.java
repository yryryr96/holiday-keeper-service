package com.holidaykeeper.holidaykeeper.dto.request;

import lombok.Getter;

@Getter
public class HolidayDeleteRequest {

    private Integer year;
    private String countryCode;
}
