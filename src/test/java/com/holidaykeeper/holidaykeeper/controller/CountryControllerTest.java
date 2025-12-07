package com.holidaykeeper.holidaykeeper.controller;

import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.dto.response.CountryListResponse;
import com.holidaykeeper.holidaykeeper.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryService countryService;

    @Test
    @DisplayName("GET /countries - 지원 국가 목록 조회 성공")
    void getCountries() throws Exception {
        // given
        List<CountryDto> countries = List.of(
                CountryDto.builder().countryCode("KR").name("South Korea").build(),
                CountryDto.builder().countryCode("US").name("United States").build(),
                CountryDto.builder().countryCode("JP").name("Japan").build()
        );
        CountryListResponse response = CountryListResponse.from(countries);

        given(countryService.getCountries()).willReturn(response);

        // when & then
        mockMvc.perform(get("/countries"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.countries").isArray())
                .andExpect(jsonPath("$.data.countries.length()").value(3))
                .andExpect(jsonPath("$.data.countries[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.data.countries[0].name").value("South Korea"))
                .andExpect(jsonPath("$.data.countries[1].countryCode").value("US"))
                .andExpect(jsonPath("$.data.countries[2].countryCode").value("JP"));

        // then
        verify(countryService).getCountries();
    }

    @Test
    @DisplayName("GET /countries - 국가가 없으면 빈 배열 반환")
    void getCountries_empty() throws Exception {
        // given
        CountryListResponse response = CountryListResponse.from(List.of());

        given(countryService.getCountries()).willReturn(response);

        // when & then
        mockMvc.perform(get("/countries"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.countries").isArray())
                .andExpect(jsonPath("$.data.countries.length()").value(0));

        // then
        verify(countryService).getCountries();
    }
}
