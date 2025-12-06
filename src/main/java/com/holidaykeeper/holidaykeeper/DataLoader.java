package com.holidaykeeper.holidaykeeper;

import com.holidaykeeper.holidaykeeper.client.NagerApiClient;
import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.service.CountryService;
import com.holidaykeeper.holidaykeeper.service.HolidayService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataLoader {

    private final NagerApiClient nagerApiClient;
    private final HolidayService holidayService;
    private final CountryService  countryService;

    @PostConstruct
    public void initData() {
        // 1. Country 먼저 저장 (Holiday의 FK 참조를 위해 선행 필요)
        List<CountryDto> countryDtoList = nagerApiClient.getAvailableCountries();
        List<Country> countries = countryService.saveAll(countryDtoList);

        // 2. Holiday 저장 (Country가 이미 DB에 저장된 상태)
        int DURATION = 5;
        int CURRENT_YEAR = LocalDate.now().getYear();
        int START_YEAR = CURRENT_YEAR - DURATION;

        for (Country country : countries) {
            for (int year = START_YEAR; year <= CURRENT_YEAR; year++) {
                List<HolidayDto> holidays = nagerApiClient.getPublicHolidays(year, country.getCode());
                if (holidays != null && !holidays.isEmpty()) {
                    // 각 API 호출마다 바로 저장 (메모리 효율성)
                    holidayService.saveAll(holidays, country);
                }
            }
        }
    }
}
