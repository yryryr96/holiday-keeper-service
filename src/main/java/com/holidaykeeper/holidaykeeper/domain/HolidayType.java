package com.holidaykeeper.holidaykeeper.domain;

public enum HolidayType {
    PUBLIC("Public"),
    BANK("Bank holiday, banks and offices are closed"),
    SCHOOL("School holiday, schools are closed"),
    AUTHORITIES("Authorities are closed"),
    OPTIONAL("Majority of people take a day off"),
    OBSERVANCE("Optional festivity, no paid day off");

    private final String description;

    HolidayType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}