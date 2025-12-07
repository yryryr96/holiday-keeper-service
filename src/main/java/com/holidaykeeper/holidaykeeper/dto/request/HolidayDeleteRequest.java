package com.holidaykeeper.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "공휴일 삭제 요청")
@Getter
@Builder
public class HolidayDeleteRequest {

    @NotNull(message = "연도는 필수입니다.")
    @Schema(description = "삭제할 연도", example = "2024", required = true)
    private Integer year;

    @NotBlank(message = "국가 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다.")
    @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR", required = true)
    private String countryCode;
}
