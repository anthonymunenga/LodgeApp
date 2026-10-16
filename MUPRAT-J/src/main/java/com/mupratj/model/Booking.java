package com.mupratj.model;

import java.time.LocalDateTime;

public class Booking {
    private final String billNumber;
    private final String guestName;
    private final RoomType roomType;
    private final String roomNumber;
    private final int nights;
    private final double extras;
    private final String notes;
    private final String createdBy;
    private final LocalDateTime createdAt;

    private double amountPaid;
    private boolean checkedOut;
    private boolean deleted;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public Booking(
            String billNumber,
            String guestName,
            RoomType roomType,
            String roomNumber,
            int nights,
            double extras,
            double amountPaid,
            String notes,
            String createdBy,
            LocalDateTime createdAt
    ) {
        if (nights < 1) {
            throw new IllegalArgumentException("A booking must be at least one night.");
        }

        this.billNumber = billNumber;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.nights = nights;
        this.extras = extras;
        this.amountPaid = amountPaid;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getNights() {
        return nights;
    }

    public double getRoomCharge() {
        return roomType.getNightlyRate() * nights;
    }

    public double getDiscount() {
        return nights >= 5 ? getRoomCharge() * 0.05 : 0.0;
    }

    public double getExtras() {
        return extras;
    }

    public double getTotal() {
        return getRoomCharge() - getDiscount() + extras;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getBalance() {
        return getTotal() - amountPaid;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void checkOut() {
        checkedOut = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void markDeleted(String username, LocalDateTime deletedAt) {
        this.deleted = true;
        this.deletedBy = username;
        this.deletedAt = deletedAt;
    }

    public void restore() {
        deleted = false;
        deletedBy = null;
        deletedAt = null;
    }

    public void recordPayment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment must be greater than zero.");
        }
        amountPaid += amount;
    }
}