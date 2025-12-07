package com.holidaykeeper.holidaykeeper.controller;

import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayRefreshRequest;
import com.holidaykeeper.holidaykeeper.dto.response.ApiResponse;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Holiday", description = "공휴일 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
@Validated
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(summary = "공휴일 조회", description = "연도, 국가 코드, 날짜 범위 등의 필터로 공휴일을 페이징 조회합니다.")
    @GetMapping
    public ApiResponse<PageResponse<HolidayResponse>> getHolidays(@Valid @ModelAttribute HolidayGetRequest request) {
        System.out.println("request.getCountryCode() = " + request.getCountryCode());
        return ApiResponse.ok(holidayService.getHolidays(request));
    }

    @Operation(summary = "공휴일 재동기화", description = "특정 연도·국가의 공휴일 데이터를 외부 API에서 다시 가져와 Upsert 합니다.")
    @PostMapping("/refresh")
    public ApiResponse<Void> refreshHolidays(@Valid @RequestBody HolidayRefreshRequest request) {
        holidayService.refreshHolidays(request);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "공휴일 삭제", description = "특정 연도·국가의 모든 공휴일 데이터를 삭제합니다.")
    @DeleteMapping
    public ApiResponse<Void> deleteHolidays(@Valid @RequestBody HolidayDeleteRequest request) {
        holidayService.deleteHolidays(request);
        return ApiResponse.of(HttpStatus.NO_CONTENT, null);
    }
}
