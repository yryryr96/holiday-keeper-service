package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.*;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NagerHolidayService implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final CountyService countyService;

    @Transactional
    @Override
    public void saveAll(List<HolidayDto> holidayDtoList, Country country) {
        List<Holiday> holidays = holidayDtoList.stream()
                .map(dto -> this.convertToHoliday(dto, country))
                .toList();

        holidayRepository.saveAll(holidays);
    }

    private Holiday convertToHoliday(HolidayDto holidayDto, Country country) {
        // 1. Holiday 엔티티 생성
        Holiday holiday = Holiday.builder()
                .country(country)
                .date(holidayDto.getDate())
                .localName(holidayDto.getLocalName())
                .name(holidayDto.getName())
                .global(holidayDto.getGlobal() != null ? holidayDto.getGlobal() : false)
                .launchYear(holidayDto.getLaunchYear() != null ? Year.of(holidayDto.getLaunchYear()) : null)
                .build();

        // 2. HolidayType 연관관계 설정
        holidayDto.convertToHolidayTypes().forEach(type -> {
            HolidayTypeMap typeMap = HolidayTypeMap.builder()
                    .type(type)
                    .build();
            holiday.addType(typeMap);
        });

        // 3. County 연관관계 설정
        if (holidayDto.getCounties() != null && !holidayDto.getCounties().isEmpty()) {
            // County 먼저 저장 (없으면 생성, 있으면 조회)
            List<County> counties = countyService.saveCounties(holidayDto.getCounties());

            // HolidayCountyMap 생성 및 양방향 연관관계 설정
            counties.forEach(county -> {
                HolidayCountyMap countyMap = HolidayCountyMap.builder()
                        .county(county)
                        .build();
                holiday.addCounty(countyMap);
            });
        }

        return holiday;
    }
}
