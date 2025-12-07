package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.Country;
import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.dto.response.CountryListResponse;
import com.holidaykeeper.holidaykeeper.exception.CountryNotFoundException;
import com.holidaykeeper.holidaykeeper.repository.CountryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NagerCountryServiceTest {

    private static final String TEST_COUNTRY_CODE = "KR";
    private static final String TEST_COUNTRY_NAME = "South Korea";
    private static final String INVALID_COUNTRY_CODE = "XX";

    @Autowired
    private CountryService countryService;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("getCountries - 저장된 국가 목록 조회")
    void getCountries() {
        // given
        Country country1 = Country.builder().code("KR").name("South Korea").build();
        Country country2 = Country.builder().code("US").name("United States").build();
        Country country3 = Country.builder().code("JP").name("Japan").build();
        countryRepository.saveAll(List.of(country1, country2, country3));

        // when
        CountryListResponse result = countryService.getCountries();

        // then
        assertThat(result.getCountries()).hasSize(3);
        assertThat(result.getCountries())
                .extracting(CountryDto::getCountryCode)
                .containsExactlyInAnyOrder("KR", "US", "JP");
        assertThat(result.getCountries())
                .extracting(CountryDto::getName)
                .containsExactlyInAnyOrder("South Korea", "United States", "Japan");
    }

    @Test
    @DisplayName("getCountries - 국가가 없으면 빈 리스트 반환")
    void getCountries_empty() {
        // when
        CountryListResponse result = countryService.getCountries();

        // then
        assertThat(result.getCountries()).isEmpty();
    }

    @Test
    @DisplayName("saveAll - 국가 목록 저장")
    void saveAll() {
        // given
        List<CountryDto> countryDtos = List.of(
                CountryDto.builder().countryCode("KR").name("South Korea").build(),
                CountryDto.builder().countryCode("US").name("United States").build()
        );

        // when
        List<Country> result = countryService.saveAll(countryDtos);

        // then
        assertThat(result).hasSize(2);
        assertThat(countryRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("findByCode - 국가 코드로 조회")
    void findByCode() {
        // given
        Country country = Country.builder().code(TEST_COUNTRY_CODE).name(TEST_COUNTRY_NAME).build();
        countryRepository.save(country);

        // when
        Country result = countryService.findByCode(TEST_COUNTRY_CODE);

        // then
        assertThat(result.getCode()).isEqualTo(TEST_COUNTRY_CODE);
        assertThat(result.getName()).isEqualTo(TEST_COUNTRY_NAME);
    }

    @Nested
    @DisplayName("예외 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("findByCode - 존재하지 않는 국가 코드면 CountryNotFoundException 발생")
        void findByCode_notFound_throwsException() {
            // when & then
            assertThatThrownBy(() -> countryService.findByCode(INVALID_COUNTRY_CODE))
                    .isInstanceOf(CountryNotFoundException.class)
                    .hasMessageContaining(INVALID_COUNTRY_CODE);
        }
    }
}
