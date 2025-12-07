package com.holidaykeeper.holidaykeeper.controller;

import com.holidaykeeper.holidaykeeper.dto.response.ApiResponse;
import com.holidaykeeper.holidaykeeper.dto.response.CountryListResponse;
import com.holidaykeeper.holidaykeeper.service.CountryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Country", description = "지원 국가 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/countries")
@Validated
public class CountryController {

    private final CountryService  countryService;

    @GetMapping
    public ApiResponse<CountryListResponse> getCountries() {
        return ApiResponse.ok(countryService.getCountries());
    }
}
