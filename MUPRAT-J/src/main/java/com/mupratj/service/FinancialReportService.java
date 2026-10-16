package com.mupratj.service;

import com.mupratj.model.Booking;
import com.mupratj.model.FinancialPeriod;
import com.mupratj.model.FinancialSummary;
import com.mupratj.model.User;

import java.time.LocalDate;
import java.util.List;

public class FinancialReportService {
    private final BookingService bookingService;

    public FinancialReportService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public FinancialSummary createReport(
            User user,
            FinancialPeriod period
    ) {
        if (user == null || period == null) {
            throw new IllegalArgumentException("A user and report period are required.");
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = period.getStartDate(today);
        LocalDate endDate = period.getEndDate(today);

        List<Booking> bookings = bookingService
                .getBookingsVisibleTo(user)
                .stream()
                .filter(booking -> booking.getCreatedAt() != null)
                .filter(booking -> {
                    LocalDate bookingDate = booking.getCreatedAt().toLocalDate();
                    return !bookingDate.isBefore(startDate)
                            && !bookingDate.isAfter(endDate);
                })
                .toList();

        return new FinancialSummary(period, bookings);
    }
}