package com.mupratj.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FinancialSummary {
    private final FinancialPeriod period;
    private final List<Booking> bookings;

    public FinancialSummary(FinancialPeriod period, List<Booking> bookings) {
        this.period = period;
        this.bookings = List.copyOf(bookings);
    }

    public FinancialPeriod getPeriod() {
        return period;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public int getBillCount() {
        return bookings.size();
    }

    public double getTotalRevenue() {
        return bookings.stream()
                .mapToDouble(Booking::getTotal)
                .sum();
    }

    public double getTotalPaid() {
        return bookings.stream()
                .mapToDouble(Booking::getAmountPaid)
                .sum();
    }

    public double getTotalBalance() {
        return bookings.stream()
                .mapToDouble(Booking::getBalance)
                .sum();
    }

    public Map<String, Double> getCustomerBalances() {
        Map<String, Double> balances = new LinkedHashMap<>();

        for (Booking booking : bookings) {
            balances.merge(
                    booking.getGuestName(),
                    booking.getBalance(),
                    Double::sum
            );
        }

        return balances;
    }
}