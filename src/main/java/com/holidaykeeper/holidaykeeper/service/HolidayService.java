package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;

import java.util.List;

public interface HolidayService {

    void saveAll(List<HolidayDto> holidayDtoList, Country country);
}
