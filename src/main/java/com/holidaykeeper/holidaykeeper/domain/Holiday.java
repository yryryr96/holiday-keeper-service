package com.holidaykeeper.holidaykeeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "holiday")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "date")
    @Comment("날짜")
    private LocalDate date;

    @Column(name = "local_name")
    @Comment("지역명")
    private String localName;

    @Column(name = "name")
    @Comment("공휴일 명")
    private String name;
    
    @Column(name = "global")
    @Comment("글로벌 여부")
    private boolean global;

    @OneToMany(mappedBy = "holiday", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HolidayCountyMap> counties = new ArrayList<>();

    @OneToMany(mappedBy = "holiday", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HolidayTypeMap> types = new ArrayList<>();

    @Column(name = "launch_year")
    @Comment("출시 년도")
    private Year launchYear;

    @Builder
    public Holiday(Long id, Country country, LocalDate date, String localName, String name, boolean global,
                   List<HolidayCountyMap> counties, List<HolidayTypeMap> types, Year launchYear) {
        this.id = id;
        this.country = country;
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.global = global;
        this.counties = counties != null ? counties : new ArrayList<>();
        this.types = types != null ? types : new ArrayList<>();
        this.launchYear = launchYear;
    }

    public void addType(HolidayTypeMap typeMap) {
        this.types.add(typeMap);
        typeMap.setHoliday(this);
    }

    public void addCounty(HolidayCountyMap countyMap) {
        this.counties.add(countyMap);
        countyMap.setHoliday(this);
    }
}
