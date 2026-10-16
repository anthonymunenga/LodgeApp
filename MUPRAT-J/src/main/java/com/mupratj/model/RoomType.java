package com.mupratj.model;

public enum RoomType {
    STANDARD("Standard", 350.00, 30),
    FAMILY("Family", 750.00, 50),
    PREMIUM("Premium", 550.00, 20);

    private final String displayName;
    private final double nightlyRate;
    private final int roomCount;

    RoomType(String displayName, double nightlyRate, int roomCount) {
        this.displayName = displayName;
        this.nightlyRate = nightlyRate;
        this.roomCount = roomCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getNightlyRate() {
        return nightlyRate;
    }

    public int getRoomCount() {
        return roomCount;
    }

    @Override
    public String toString() {
        return displayName;
    }
}