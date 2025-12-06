package com.holidaykeeper.holidaykeeper.repository;

import com.holidaykeeper.holidaykeeper.domain.Holiday;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;

public interface HolidayRepositoryCustom {

    PageResponse<Holiday> getHolidays(HolidayGetRequest request);
}
