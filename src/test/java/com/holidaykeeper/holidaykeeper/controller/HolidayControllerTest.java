package com.holidaykeeper.holidaykeeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayRefreshRequest;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

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
                .date(LocalDate.of(2024, 1, 1))
                .name("New Year's Day")
                .localName("신정")
                .countryCode("KR")
                .global(true)
                .counties(List.of())
                .launchYear(1949)
                .types(List.of("PUBLIC"))
                .build();

        PageResponse<HolidayResponse> pageResponse = PageResponse.<HolidayResponse>builder()
                .content(List.of(response))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build();

        given(holidayService.getHolidays(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/holidays")
                        .param("year", "2024")
                        .param("countryCode", "KR")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("New Year's Day"))
                .andExpect(jsonPath("$.data.content[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /holidays - 날짜 범위 필터링")
    void getHolidaysWithDateRange() throws Exception {
        // given
        PageResponse<HolidayResponse> pageResponse = PageResponse.<HolidayResponse>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .build();

        given(holidayService.getHolidays(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/holidays")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-12-31")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /holidays/refresh - 공휴일 재동기화 성공")
    void refreshHolidays() throws Exception {
        // given
        HolidayRefreshRequest request = HolidayRefreshRequest.builder()
                .year(2024)
                .countryCode("KR")
                .build();

        doNothing().when(holidayService).refreshHolidays(any());

        // when & then
        mockMvc.perform(post("/holidays/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /holidays - 공휴일 삭제 성공")
    void deleteHolidays() throws Exception {
        // given
        HolidayDeleteRequest request = HolidayDeleteRequest.builder()
                .year(2024)
                .countryCode("KR")
                .build();

        doNothing().when(holidayService).deleteHolidays(any());

        // when & then
        mockMvc.perform(delete("/holidays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(204));
    }
}
