package com.holidaykeeper.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다.")
    @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR")
    private String countryCode;

    @Schema(description = "조회 시작 날짜", example = "2024-01-01")
    private LocalDate fromDate;

    @Schema(description = "조회 종료 날짜", example = "2024-12-31")
    private LocalDate toDate;

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    private Integer page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    private Integer size;
}
