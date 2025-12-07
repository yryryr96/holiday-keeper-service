package com.holidaykeeper.holidaykeeper.exception;

public class CountryNotFoundException extends BusinessException {

    public CountryNotFoundException(String countryCode) {
        super(ErrorCode.COUNTRY_NOT_FOUND, "존재하지 않는 국가 코드입니다: " + countryCode);
    }
}
