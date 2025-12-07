package com.holidaykeeper.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "공휴일 재동기화 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayRefreshRequest {

    @NotNull(message = "연도는 필수입니다.")
    @Schema(description = "재동기화할 연도", example = "2024", required = true)
    private Integer year;

    @NotBlank(message = "국가 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다.")
    @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR", required = true)
    private String countryCode;
}
