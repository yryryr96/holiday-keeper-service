package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayRefreshRequest;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;

import java.util.List;

public interface HolidayService {

    void saveAll(List<HolidayDto> holidayDtoList, Country country);

    PageResponse<HolidayResponse> getHolidays(HolidayGetRequest request);

    void refreshHolidays(HolidayRefreshRequest request);

    void deleteHolidays(HolidayDeleteRequest request);
}
