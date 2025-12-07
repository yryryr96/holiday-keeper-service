package com.holidaykeeper.holidaykeeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayCountyMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id")
    private County county;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    @Builder
    private HolidayCountyMap(Long id, County county, Holiday holiday) {
        this.id = id;
        this.county = county;
        this.holiday = holiday;
    }

    public void setHoliday(Holiday holiday) {
        this.holiday = holiday;
    }
}
