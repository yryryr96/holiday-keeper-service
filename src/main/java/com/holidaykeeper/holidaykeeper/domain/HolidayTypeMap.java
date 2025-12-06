package com.holidaykeeper.holidaykeeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "holiday_type_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayTypeMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private HolidayType type;

    @Builder
    public HolidayTypeMap(Long id, Holiday holiday, HolidayType type) {
        this.id = id;
        this.holiday = holiday;
        this.type = type;
    }

    public void setHoliday(Holiday holiday) {
        this.holiday = holiday;
    }
}