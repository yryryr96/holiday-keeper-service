package com.holidaykeeper.holidaykeeper.repository;

import com.holidaykeeper.holidaykeeper.domain.County;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountyRepository extends JpaRepository<County, Long> {

    @Query(
            "SELECT c FROM County c " +
            "WHERE c.name IN :countyNames"
    )
    List<County> findAllByCountyNames(List<String> countyNames);
}
