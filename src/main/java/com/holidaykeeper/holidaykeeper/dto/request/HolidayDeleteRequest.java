package com.holidaykeeper.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "공휴일 삭제 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayDeleteRequest {

    @Schema(description = "삭제할 연도", example = "2024", required = true)
    private Integer year;

    @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR", required = true)
    private String countryCode;
}
