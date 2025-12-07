package com.holidaykeeper.holidaykeeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayRefreshRequest;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.exception.CountryNotFoundException;
import com.holidaykeeper.holidaykeeper.exception.ExternalApiException;
import com.holidaykeeper.holidaykeeper.service.HolidayService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

    private static final int TEST_YEAR = 2024;
    private static final String TEST_COUNTRY_CODE = "KR";
    private static final String INVALID_COUNTRY_CODE = "XX";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 1, 1);
    private static final String TEST_HOLIDAY_NAME = "New Year's Day";
    private static final String TEST_HOLIDAY_LOCAL_NAME = "신정";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HolidayService holidayService;

    @Test
    @DisplayName("GET /holidays - 공휴일 조회 성공")
    void getHolidays() throws Exception {

        // given
        HolidayResponse response = HolidayResponse.builder()
                .date(TEST_DATE)
                .name(TEST_HOLIDAY_NAME)
                .localName(TEST_HOLIDAY_LOCAL_NAME)
                .countryCode(TEST_COUNTRY_CODE)
                .global(true)
                .counties(List.of())
                .launchYear(1949)
                .types(List.of("PUBLIC"))
                .build();

        PageResponse<HolidayResponse> pageResponse = PageResponse.<HolidayResponse>builder()
                .content(List.of(response))
                .page(DEFAULT_PAGE)
                .size(DEFAULT_SIZE)
                .totalElements(1)
                .totalPages(1)
                .build();

        given(holidayService.getHolidays(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/holidays")
                        .param("year", String.valueOf(TEST_YEAR))
                        .param("countryCode", TEST_COUNTRY_CODE)
                        .param("page", String.valueOf(DEFAULT_PAGE))
                        .param("size", String.valueOf(DEFAULT_SIZE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value(TEST_HOLIDAY_NAME))
                .andExpect(jsonPath("$.data.content[0].countryCode").value(TEST_COUNTRY_CODE))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        // then
        ArgumentCaptor<HolidayGetRequest> requestCaptor = ArgumentCaptor.forClass(HolidayGetRequest.class);

        verify(holidayService).getHolidays(requestCaptor.capture());

        HolidayGetRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.getYear()).isEqualTo(TEST_YEAR);
        assertThat(capturedRequest.getCountryCode()).isEqualTo(TEST_COUNTRY_CODE);
        assertThat(capturedRequest.getPage()).isEqualTo(DEFAULT_PAGE);
        assertThat(capturedRequest.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    @DisplayName("GET /holidays - 날짜 범위 필터링")
    void getHolidaysWithDateRange() throws Exception {

        // given
        PageResponse<HolidayResponse> pageResponse = PageResponse.<HolidayResponse>builder()
                .content(List.of())
                .page(DEFAULT_PAGE)
                .size(DEFAULT_SIZE)
                .totalElements(0)
                .totalPages(0)
                .build();

        given(holidayService.getHolidays(any())).willReturn(pageResponse);

        // when
        mockMvc.perform(get("/holidays")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-12-31")
                        .param("page", String.valueOf(DEFAULT_PAGE))
                        .param("size", String.valueOf(DEFAULT_SIZE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // then
        ArgumentCaptor<HolidayGetRequest> requestCaptor = ArgumentCaptor.forClass(HolidayGetRequest.class);

        verify(holidayService).getHolidays(requestCaptor.capture());

        HolidayGetRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.getFromDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(capturedRequest.getToDate()).isEqualTo(LocalDate.of(2024, 12, 31));
    }

    @Test
    @DisplayName("POST /holidays/refresh - 공휴일 재동기화 성공")
    void refreshHolidays() throws Exception {

        // given
        HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                .year(TEST_YEAR)
                .countryCode(TEST_COUNTRY_CODE)
                .build();

        doNothing().when(holidayService).refreshHolidays(any());

        // when
        mockMvc.perform(post("/holidays/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // then
        ArgumentCaptor<HolidayRefreshRequest> requestCaptor = ArgumentCaptor.forClass(HolidayRefreshRequest.class);

        verify(holidayService).refreshHolidays(requestCaptor.capture());

        HolidayRefreshRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.getYear()).isEqualTo(TEST_YEAR);
        assertThat(capturedRequest.getCountryCode()).isEqualTo(TEST_COUNTRY_CODE);
    }

    @Test
    @DisplayName("DELETE /holidays - 공휴일 삭제 성공")
    void deleteHolidays() throws Exception {

        // given
        HolidayDeleteRequest request = HolidayDeleteRequest.builder()
                .year(TEST_YEAR)
                .countryCode(TEST_COUNTRY_CODE)
                .build();

        doNothing().when(holidayService).deleteHolidays(any());

        // when
        mockMvc.perform(delete("/holidays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(204));

        // then
        ArgumentCaptor<HolidayDeleteRequest> requestCaptor = ArgumentCaptor.forClass(HolidayDeleteRequest.class);

        verify(holidayService).deleteHolidays(requestCaptor.capture());

        HolidayDeleteRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.getYear()).isEqualTo(TEST_YEAR);
        assertThat(capturedRequest.getCountryCode()).isEqualTo(TEST_COUNTRY_CODE);
    }

    @Nested
    @DisplayName("Validation 에러 테스트")
    class ValidationTest {

        @Test
        @DisplayName("POST /holidays/refresh - year가 null이면 400 에러")
        void refreshHolidays_yearNull_returnsBadRequest() throws Exception {
            // given
            HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                    .year(null)
                    .countryCode(TEST_COUNTRY_CODE)
                    .build();

            // when & then
            mockMvc.perform(post("/holidays/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors[0].field").value("year"));
        }

        @Test
        @DisplayName("POST /holidays/refresh - countryCode가 빈 문자열이면 400 에러")
        void refreshHolidays_countryCodeBlank_returnsBadRequest() throws Exception {
            // given
            HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                    .year(TEST_YEAR)
                    .countryCode("")
                    .build();

            // when & then
            mockMvc.perform(post("/holidays/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        @DisplayName("POST /holidays/refresh - countryCode 형식이 잘못되면 400 에러")
        void refreshHolidays_countryCodeInvalidFormat_returnsBadRequest() throws Exception {
            // given
            HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                    .year(TEST_YEAR)
                    .countryCode("korea")
                    .build();

            // when & then
            mockMvc.perform(post("/holidays/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors[0].field").value("countryCode"));
        }

        @Test
        @DisplayName("DELETE /holidays - year가 null이면 400 에러")
        void deleteHolidays_yearNull_returnsBadRequest() throws Exception {
            // given
            HolidayDeleteRequest request = HolidayDeleteRequest.builder()
                    .year(null)
                    .countryCode(TEST_COUNTRY_CODE)
                    .build();

            // when & then
            mockMvc.perform(delete("/holidays")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors[0].field").value("year"));
        }
    }

    @Nested
    @DisplayName("Business 예외 테스트")
    class BusinessExceptionTest {

        @Test
        @DisplayName("POST /holidays/refresh - 존재하지 않는 국가 코드면 404 에러")
        void refreshHolidays_countryNotFound_returnsNotFound() throws Exception {
            // given
            HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                    .year(TEST_YEAR)
                    .countryCode(INVALID_COUNTRY_CODE)
                    .build();

            doThrow(new CountryNotFoundException(INVALID_COUNTRY_CODE))
                    .when(holidayService).refreshHolidays(any());

            // when & then
            mockMvc.perform(post("/holidays/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 국가 코드입니다: " + INVALID_COUNTRY_CODE));
        }

        @Test
        @DisplayName("POST /holidays/refresh - 외부 API 호출 실패시 503 에러")
        void refreshHolidays_externalApiError_returnsServiceUnavailable() throws Exception {
            // given
            HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                    .year(TEST_YEAR)
                    .countryCode(TEST_COUNTRY_CODE)
                    .build();

            doThrow(new ExternalApiException("외부 API 호출 실패"))
                    .when(holidayService).refreshHolidays(any());

            // when & then
            mockMvc.perform(post("/holidays/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.status").value(503));
        }
    }
}
