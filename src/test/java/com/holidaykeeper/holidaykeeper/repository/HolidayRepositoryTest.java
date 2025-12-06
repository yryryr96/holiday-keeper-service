package com.holidaykeeper.holidaykeeper.repository;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.domain.Holiday;
import com.holidaykeeper.holidaykeeper.domain.HolidayType;
import com.holidaykeeper.holidaykeeper.domain.HolidayTypeMap;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({HolidayRepositoryCustomImpl.class, HolidayRepositoryTest.TestConfig.class})
class HolidayRepositoryTest {

    static class TestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = Country.builder()
                .code("KR")
                .name("South Korea")
                .build();
        countryRepository.save(testCountry);
    }

    @Test
    @DisplayName("공휴일 저장 및 조회")
    void saveAndFindHoliday() {
        // given
        Holiday holiday = createHoliday(LocalDate.of(2024, 1, 1), "New Year");

        // when
        Holiday saved = holidayRepository.save(holiday);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Year");
        assertThat(saved.getCountry().getCode()).isEqualTo("KR");
    }

    @Test
    @DisplayName("QueryDSL - 연도별 조회")
    void findByYear() {
        // given
        Holiday holiday2024 = createHoliday(LocalDate.of(2024, 1, 1), "New Year 2024");
        Holiday holiday2025 = createHoliday(LocalDate.of(2025, 1, 1), "New Year 2025");
        holidayRepository.saveAll(List.of(holiday2024, holiday2025));

        HolidayGetRequest request = HolidayGetRequest.builder()
                .year(2024)
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<Holiday> result = holidayRepository.getHolidays(request);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDate().getYear()).isEqualTo(2024);
    }

    @Test
    @DisplayName("QueryDSL - 국가 코드별 조회")
    void findByCountryCode() {
        // given
        Holiday holiday = createHoliday(LocalDate.of(2024, 1, 1), "New Year");
        holidayRepository.save(holiday);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .countryCode("KR")
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<Holiday> result = holidayRepository.getHolidays(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getCountry().getCode()).isEqualTo("KR");
    }

    @Test
    @DisplayName("QueryDSL - 날짜 범위 조회")
    void findByDateRange() {
        // given
        Holiday holiday1 = createHoliday(LocalDate.of(2024, 1, 1), "Holiday 1");
        Holiday holiday2 = createHoliday(LocalDate.of(2024, 6, 6), "Holiday 2");
        Holiday holiday3 = createHoliday(LocalDate.of(2024, 12, 25), "Holiday 3");
        holidayRepository.saveAll(List.of(holiday1, holiday2, holiday3));

        HolidayGetRequest request = HolidayGetRequest.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 6, 30))
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<Holiday> result = holidayRepository.getHolidays(request);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("QueryDSL - 페이징 처리")
    void findWithPaging() {
        // given
        for (int i = 1; i <= 25; i++) {
            Holiday holiday = createHoliday(LocalDate.of(2024, 1, i), "Holiday " + i);
            holidayRepository.save(holiday);
        }

        HolidayGetRequest request1 = HolidayGetRequest.builder()
                .year(2024)
                .page(0)
                .size(10)
                .build();

        HolidayGetRequest request2 = HolidayGetRequest.builder()
                .year(2024)
                .page(1)
                .size(10)
                .build();

        // when
        PageResponse<Holiday> page1 = holidayRepository.getHolidays(request1);
        PageResponse<Holiday> page2 = holidayRepository.getHolidays(request2);

        // then
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page1.getPage()).isEqualTo(0);
        assertThat(page1.getTotalElements()).isEqualTo(25);
        assertThat(page1.getTotalPages()).isEqualTo(3);

        assertThat(page2.getContent()).hasSize(10);
        assertThat(page2.getPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fetch Join - N+1 문제 방지")
    void fetchJoinTest() {
        // given
        Holiday holiday = createHoliday(LocalDate.of(2024, 1, 1), "New Year");
        HolidayTypeMap typeMap = HolidayTypeMap.builder()
                .type(HolidayType.PUBLIC)
                .build();
        holiday.addType(typeMap);
        holidayRepository.save(holiday);

        HolidayGetRequest request = HolidayGetRequest.builder()
                .year(2024)
                .page(0)
                .size(10)
                .build();

        // when
        PageResponse<Holiday> result = holidayRepository.getHolidays(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTypes()).isNotEmpty();
        assertThat(result.getContent().get(0).getTypes().get(0).getType()).isEqualTo(HolidayType.PUBLIC);
    }

    private Holiday createHoliday(LocalDate date, String name) {
        return Holiday.builder()
                .country(testCountry)
                .date(date)
                .name(name)
                .localName(name)
                .global(true)
                .launchYear(2024)
                .build();
    }
}
