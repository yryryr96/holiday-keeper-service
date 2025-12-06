package com.holidaykeeper.holidaykeeper.dto.request;

import lombok.Getter;

@Getter
public class HolidayRefreshRequest {

    private Integer year;
    private String countryCode;
}
