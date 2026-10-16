package com.mupratj.model;

public class Room {
    private final String roomNumber;
    private final RoomType roomType;
    private RoomStatus status;

    public Room(String roomNumber, RoomType roomType) {
        this(roomNumber, roomType, RoomStatus.AVAILABLE);
    }

    public Room(String roomNumber, RoomType roomType, RoomStatus status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void occupy() {
        if (status == RoomStatus.AVAILABLE) {
            status = RoomStatus.OCCUPIED;
        }
    }

    public void checkOut() {
        if (status == RoomStatus.OCCUPIED) {
            status = RoomStatus.AVAILABLE;
        }
    }

    public void markForMaintenance() {
        status = RoomStatus.MAINTENANCE;
    }

    public void finishMaintenance() {
        if (status == RoomStatus.MAINTENANCE) {
            status = RoomStatus.AVAILABLE;
        }
    }

    @Override
    public String toString() {
        return roomNumber + " — " + roomType.getDisplayName()
                + " (" + status + ")";
    }
}