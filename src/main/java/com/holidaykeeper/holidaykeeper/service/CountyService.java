package com.holidaykeeper.holidaykeeper.service;

import com.holidaykeeper.holidaykeeper.domain.County;
import com.holidaykeeper.holidaykeeper.repository.CountyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountyService {

    private final CountyRepository countyRepository;

    @Transactional
    public List<County> saveCounties(List<String> countyNames) {
        if (countyNames == null || countyNames.isEmpty()) {
            return List.of();
        }

        // 이미 존재하는 County 조회
        List<County> existingCounties = countyRepository.findAllByCountyNames(countyNames);
        List<String> existingCountyNames = existingCounties.stream()
                .map(County::getName)
                .toList();

        // 새로운 County만 저장
        List<County> newCounties = countyNames.stream()
                .filter(name -> !existingCountyNames.contains(name))
                .map(name -> County.builder().name(name).build())
                .toList();

        if (!newCounties.isEmpty()) {
            countyRepository.saveAll(newCounties);
        }

        // 전체 County 반환
        List<County> allCounties = new ArrayList<>(existingCounties);
        allCounties.addAll(newCounties);
        return allCounties;
    }
}
