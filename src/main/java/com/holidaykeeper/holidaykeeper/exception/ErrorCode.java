package com.holidaykeeper.holidaykeeper.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "잘못된 타입입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // Country
    COUNTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 국가 코드입니다."),

    // External API
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 API 호출에 실패했습니다."),
    EXTERNAL_API_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "외부 API 응답 시간이 초과되었습니다.");

    private final HttpStatus status;
    private final String message;

    public int getCode() {
        return status.value();
    }
}
