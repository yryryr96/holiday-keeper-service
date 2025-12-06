package com.holidaykeeper.holidaykeeper.client;

import com.holidaykeeper.holidaykeeper.dto.CountryDto;
import com.holidaykeeper.holidaykeeper.dto.HolidayDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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

        ResponseEntity<List<CountryDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CountryDto>>() {}
        );

        return response.getBody();
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
        } catch (Exception e) {
            log.warn("Failed to fetch holidays for {}/{}: {}", year, countryCode, e.getMessage());
            return List.of();
        }
    }
}
