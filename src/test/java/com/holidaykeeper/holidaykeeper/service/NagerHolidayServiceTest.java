package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.domain.Holiday;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.exception.CountryNotFoundException;
import com.holidaykeeper.holidaykeeper.repository.CountryRepository;
import com.holidaykeeper.holidaykeeper.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NagerHolidayServiceTest {

    private static final int TEST_YEAR = 2024;
    private static final String TEST_COUNTRY_CODE = "KR";
    private static final String TEST_COUNTRY_NAME = "South Korea";
    private static final String INVALID_COUNTRY_CODE = "XX";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = countryRepository.findByCode(TEST_COUNTRY_CODE);
        if (testCountry == null) {
            testCountry = Country.builder()
                    .code(TEST_COUNTRY_CODE)
                    .name(TEST_COUNTRY_NAME)
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
                .year(TEST_YEAR)
                .page(DEFAULT_PAGE)
                .size(DEFAULT_SIZE)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getDate().getYear()).isEqualTo(TEST_YEAR);
    }

    @Test
    @DisplayName("공휴일 조회 - 국가 코드 필터링")
    void getHolidaysByCountryCode() {
        // given
        HolidayDto holidayDto = createHolidayDto("2024-01-01", "New Year");
        holidayService.saveAll(List.of(holidayDto), testCountry);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .countryCode(TEST_COUNTRY_CODE)
                .page(DEFAULT_PAGE)
                .size(DEFAULT_SIZE)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getCountryCode()).isEqualTo(TEST_COUNTRY_CODE);
    }

    @Test
    @DisplayName("공휴일 조회 - 날짜 범위 필터링")
    void getHolidaysByDateRange() {
        // given
        HolidayDto holiday1 = createHolidayDto("2024-01-01", "New Year");
        HolidayDto holiday2 = createHolidayDto("2024-12-25", "Christmas");
        holidayService.saveAll(List.of(holiday1, holiday2), testCountry);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .fromDate(LocalDate.of(TEST_YEAR, 1, 1))
                .toDate(LocalDate.of(TEST_YEAR, 6, 30))
                .page(DEFAULT_PAGE)
                .size(DEFAULT_SIZE)
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
                .year(TEST_YEAR)
                .page(DEFAULT_PAGE)
                .size(DEFAULT_SIZE)
                .build();

        // when
        PageResponse<HolidayResponse> result = holidayService.getHolidays(request);

        // then
        assertThat(result.getSize()).isEqualTo(DEFAULT_SIZE);
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
                .year(TEST_YEAR)
                .countryCode(TEST_COUNTRY_CODE)
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
                .countryCode(TEST_COUNTRY_CODE)
                .global(true)
                .counties(List.of())
                .launchYear(TEST_YEAR)
                .types(List.of("PUBLIC"))
                .build();
    }

    @Nested
    @DisplayName("예외 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("존재하지 않는 국가 코드로 조회시 CountryNotFoundException 발생")
        void findByCode_notFound_throwsException() {
            // when & then
            assertThatThrownBy(() -> countryService.findByCode(INVALID_COUNTRY_CODE))
                    .isInstanceOf(CountryNotFoundException.class)
                    .hasMessageContaining(INVALID_COUNTRY_CODE);
        }
    }
}
