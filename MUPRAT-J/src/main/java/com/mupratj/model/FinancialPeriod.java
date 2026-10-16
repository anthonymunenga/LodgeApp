package com.mupratj.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum FinancialPeriod {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    THIS_YEAR("This Year");

    private final String displayName;

    FinancialPeriod(String displayName) {
        this.displayName = displayName;
    }

    public LocalDate getStartDate(LocalDate today) {
        return switch (this) {
            case TODAY -> today;
            case THIS_WEEK -> today.with(DayOfWeek.MONDAY);
            case THIS_MONTH -> today.withDayOfMonth(1);
            case THIS_YEAR -> today.withDayOfYear(1);
        };
    }

    public LocalDate getEndDate(LocalDate today) {
        return today;
    }

    @Override
    public String toString() {
        return displayName;
    }
}