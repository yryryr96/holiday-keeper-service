package com.holidaykeeper.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "공휴일 재동기화 요청")
@Getter
public class HolidayRefreshRequest {

    @Schema(description = "재동기화할 연도", example = "2024", required = true)
    private Integer year;

    @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR", required = true)
    private String countryCode;
}
