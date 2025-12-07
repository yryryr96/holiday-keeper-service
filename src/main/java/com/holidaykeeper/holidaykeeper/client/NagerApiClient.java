package com.holidaykeeper.holidaykeeper.client;

import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import com.holidaykeeper.holidaykeeper.exception.ErrorCode;
import com.holidaykeeper.holidaykeeper.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class NagerApiClient {

    private static final String BASE_URL = "https://date.nager.at/api/v3";
    private final RestTemplate restTemplate;

    public NagerApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<CountryDto> getAvailableCountries() {
        String url = BASE_URL + "/AvailableCountries";
        log.info("Fetching available countries from: {}", url);

        try {
            ResponseEntity<List<CountryDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CountryDto>>() {}
            );

            return response.getBody();
        } catch (ResourceAccessException e) {
            log.error("Timeout while fetching available countries: {}", e.getMessage());
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT,
                    "외부 API 응답 시간이 초과되었습니다: " + e.getMessage());
        } catch (RestClientException e) {
            log.error("Failed to fetch available countries: {}", e.getMessage());
            throw new ExternalApiException("국가 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public List<HolidayDto> getPublicHolidays(int year, String countryCode) {
        String url = String.format("%s/PublicHolidays/%d/%s", BASE_URL, year, countryCode);
        log.info("Fetching public holidays from: {}", url);

        try {
            ResponseEntity<List<HolidayDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<HolidayDto>>() {}
            );

            return response.getBody();
        } catch (ResourceAccessException e) {
            log.error("Timeout while fetching holidays for {}/{}: {}", year, countryCode, e.getMessage());
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT,
                    String.format("외부 API 응답 시간이 초과되었습니다 (%d/%s): %s", year, countryCode, e.getMessage()));
        } catch (RestClientException e) {
            log.error("Failed to fetch holidays for {}/{}: {}", year, countryCode, e.getMessage());
            throw new ExternalApiException(
                    String.format("공휴일 조회에 실패했습니다 (%d/%s): %s", year, countryCode, e.getMessage()));
        }
    }
}
