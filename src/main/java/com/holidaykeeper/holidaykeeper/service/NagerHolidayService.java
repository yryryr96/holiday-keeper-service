package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.client.NagerApiClient;
import com.holidaykeeper.holidaykeeper.domain.*;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayRefreshRequest;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NagerHolidayService implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final CountryService countryService;
    private final CountyService countyService;
    private final NagerApiClient nagerApiClient;

    @Transactional
    @Override
    public void saveAll(List<HolidayDto> holidayDtoList, Country country) {
        List<Holiday> holidays = holidayDtoList.stream()
                .map(dto -> this.convertToHoliday(dto, country))
                .toList();

        holidayRepository.saveAll(holidays);
    }

    @Transactional
    @Override
    public void refreshHolidays(HolidayRefreshRequest request) {
        // 1. API에서 최신 Holiday 데이터 조회
        List<HolidayDto> holidayDtoList = nagerApiClient.getPublicHolidays(request.getYear(), request.getCountryCode());

        // 2. Country 조회
        Country country = countryService.findByCode(request.getCountryCode());

        // 3. DB에서 기존 Holiday 전체 조회 (페이징 순회)
        List<Holiday> existingHolidays = getAllHolidaysByYearAndCountry(request.getYear(), request.getCountryCode());

        // 4. 기존 Holiday를 Map으로 변환 (date를 key로)
        Map<LocalDate, Holiday> existingHolidayMap = existingHolidays.stream()
                .collect(Collectors.toMap(Holiday::getDate, h -> h));

        int insertCount = 0;
        int updateCount = 0;

        // 5. Upsert 로직
        for (HolidayDto dto : holidayDtoList) {
            Holiday existingHoliday = existingHolidayMap.get(dto.getDate());
            Holiday newHoliday = convertToHoliday(dto, country);

            if (existingHoliday != null) {
                // Update: 기존 Holiday 업데이트
                existingHoliday.update(newHoliday);
                updateCount++;
            } else {
                // Insert: 새로운 Holiday 생성
                holidayRepository.save(newHoliday);
                insertCount++;
            }
        }

        log.info("Refreshed holidays for country: {}, year: {} - Inserted: {}, Updated: {}",
                request.getCountryCode(), request.getYear(), insertCount, updateCount);
    }

    @Transactional
    @Override
    public void deleteHolidays(HolidayDeleteRequest request) {
        List<Holiday> holidays = getAllHolidaysByYearAndCountry(request.getYear(), request.getCountryCode());
        holidayRepository.deleteAll(holidays);
    }

    /**
     * 모든 페이지를 순회하면서 Holiday 데이터 조회
     */
    private List<Holiday> getAllHolidaysByYearAndCountry(Integer year, String countryCode) {

        List<Holiday> allHolidays = new ArrayList<>();
        int curPage = 0;
        PageResponse<Holiday> pageResponse;

        do {
            HolidayGetRequest pageRequest = HolidayGetRequest.builder()
                    .year(year)
                    .countryCode(countryCode)
                    .page(curPage)
                    .build();

            pageResponse = holidayRepository.getHolidays(pageRequest);
            allHolidays.addAll(pageResponse.getContent());

            curPage++;
        } while (curPage < pageResponse.getTotalPages());

        return allHolidays;
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<HolidayResponse> getHolidays(HolidayGetRequest request) {
        PageResponse<Holiday> holidays = holidayRepository.getHolidays(request);
        return holidays.map(HolidayResponse::from);
    }

    private Holiday convertToHoliday(HolidayDto holidayDto, Country country) {
        // 1. Holiday 엔티티 생성
        Holiday holiday = Holiday.builder()
                .country(country)
                .date(holidayDto.getDate())
                .localName(holidayDto.getLocalName())
                .name(holidayDto.getName())
                .global(holidayDto.getGlobal() != null ? holidayDto.getGlobal() : false)
                .launchYear(holidayDto.getLaunchYear() != null ? holidayDto.getLaunchYear() : null)
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
