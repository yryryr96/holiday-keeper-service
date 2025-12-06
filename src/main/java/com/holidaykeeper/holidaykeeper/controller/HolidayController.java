package com.holidaykeeper.holidaykeeper.controller;

import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.response.ApiResponse;
import com.holidaykeeper.holidaykeeper.dto.response.HolidayResponse;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.holidaykeeper.holidaykeeper.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ApiResponse<PageResponse<HolidayResponse>> getHolidays(@ModelAttribute HolidayGetRequest request) {
        return ApiResponse.ok(holidayService.getHolidays(request));
    }
}
