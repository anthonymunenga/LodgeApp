package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class Bill {

    // Static pricing map for the rooms
    public static final Map<String, Double> ROOM_PRICES = new HashMap<>();
    static {
        ROOM_PRICES.put("Single Room", 150.0);
        ROOM_PRICES.put("Double Room", 250.0);
        ROOM_PRICES.put("Executive Suite", 450.0);
    }

    private String guestName;
    private String roomType;
    private double nightlyRate;
    private int nights;
    private double totalAmount;

    // Constructor
    public Bill(String guestName, String roomType, double nightlyRate, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nightlyRate = nightlyRate;
        this.nights = nights;
        this.totalAmount = nightlyRate * nights;
    }

    // Getters
    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getNightlyRate() {
        return nightlyRate;
    }

    public int getNights() {
        return nights;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}