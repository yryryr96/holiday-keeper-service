package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.domain.Holiday;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.repository.CountryRepository;
import com.holidaykeeper.holidaykeeper.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NagerHolidayServiceTest {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        // 테스트용 국가 데이터 조회 또는 생성
        testCountry = countryRepository.findByCode("KR");
        if (testCountry == null) {
            testCountry = Country.builder()
                    .code("KR")
                    .name("South Korea")
                    .build();
            testCountry = countryRepository.save(testCountry);
        }
    }

    @Test
    @DisplayName("공휴일 조회 - 연도별 필터링")
    void getHolidaysByYear() {
        // given
        HolidayDto holidayDto = createHolidayDto("2024-01-01", "New Year");
        holidayService.saveAll(List.of(holidayDto), testCountry);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .year(2024)
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getDate().getYear()).isEqualTo(2024);
    }

    @Test
    @DisplayName("공휴일 조회 - 국가 코드 필터링")
    void getHolidaysByCountryCode() {
        // given
        HolidayDto holidayDto = createHolidayDto("2024-01-01", "New Year");
        holidayService.saveAll(List.of(holidayDto), testCountry);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .countryCode("KR")
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getCountryCode()).isEqualTo("KR");
    }

    @Test
    @DisplayName("공휴일 조회 - 날짜 범위 필터링")
    void getHolidaysByDateRange() {
        // given
        HolidayDto holiday1 = createHolidayDto("2024-01-01", "New Year");
        HolidayDto holiday2 = createHolidayDto("2024-12-25", "Christmas");
        holidayService.saveAll(List.of(holiday1, holiday2), testCountry);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 6, 30))
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("New Year");
    }

    @Test
    @DisplayName("공휴일 조회 - 페이징")
    void getHolidaysWithPaging() {
        // given
        for (int i = 1; i <= 15; i++) {
            HolidayDto holidayDto = createHolidayDto("2024-01-" + String.format("%02d", i), "Holiday " + i);
            holidayService.saveAll(List.of(holidayDto), testCountry);
        }

        HolidayGetRequest request = HolidayGetRequest.builder()
                .year(2024)
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(15);
        assertThat(result.getTotalPages()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("공휴일 삭제 - 연도와 국가 코드로 삭제")
    void deleteHolidays() {
        // given
        HolidayDto holidayDto = createHolidayDto("2024-01-01", "New Year");
        holidayService.saveAll(List.of(holidayDto), testCountry);

        HolidayDeleteRequest request = HolidayDeleteRequest.builder()
                .year(2024)
                .countryCode("KR")
                .build();

        // when
        holidayService.deleteHolidays(request);

        // then
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).isEmpty();
    }

    private HolidayDto createHolidayDto(String date, String name) {
        return HolidayDto.builder()
                .date(LocalDate.parse(date))
                .name(name)
                .localName(name)
                .countryCode("KR")
                .global(true)
                .counties(List.of())
                .launchYear(2024)
                .types(List.of("PUBLIC"))
                .build();
    }
}
