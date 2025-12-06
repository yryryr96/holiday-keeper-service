package com.holidaykeeper.holidaykeeper.controller;

import com.holidaykeeper.holidaykeeper.dto.request.HolidayDeleteRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayRefreshRequest;
import com.holidaykeeper.holidaykeeper.dto.response.ApiResponse;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ApiResponse<PageResponse<HolidayResponse>> getHolidays(@ModelAttribute HolidayGetRequest request) {
        return ApiResponse.ok(holidayService.getHolidays(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<Void> refreshHolidays(@RequestBody HolidayRefreshRequest request) {
        holidayService.refreshHolidays(request);
        return ApiResponse.ok(null);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteHolidays(@RequestBody HolidayDeleteRequest request) {
        holidayService.deleteHolidays(request);
        return ApiResponse.of(HttpStatus.NO_CONTENT, null);
    }
}
