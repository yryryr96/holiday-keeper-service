package com.holidaykeeper.holidaykeeper.exception;

public class ExternalApiException extends BusinessException {

    public ExternalApiException(String message) {
        super(ErrorCode.EXTERNAL_API_ERROR, message);
    }

    public ExternalApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
