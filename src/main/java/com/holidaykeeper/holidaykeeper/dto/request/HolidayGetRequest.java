package com.holidaykeeper.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "공휴일 조회 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayGetRequest {

    @Schema(description = "조회할 연도", example = "2024")
    private Integer year;

    @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR")
    private String countryCode;

    @Schema(description = "조회 시작 날짜", example = "2024-01-01")
    private LocalDate fromDate;

    @Schema(description = "조회 종료 날짜", example = "2024-12-31")
    private LocalDate toDate;

    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    private Integer page;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    private Integer size;
}
