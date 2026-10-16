package com.mupratj.service;

import com.mupratj.model.Room;
import com.mupratj.model.RoomStatus;
import com.mupratj.model.RoomType;
import com.mupratj.model.User;
import com.mupratj.repository.RoomRepository;

import java.util.List;

public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms(RoomType roomType) {
        if (roomType == null) {
            return List.of();
        }

        return roomRepository.findAvailableByType(roomType);
    }

    public boolean checkOut(String roomNumber) {
        return roomRepository.checkOut(roomNumber);
    }

    public boolean markForMaintenance(String roomNumber, User user) {
        if (user == null || !user.isAdmin()) {
            return false;
        }

        return roomRepository.markForMaintenance(roomNumber);
    }

    public long countRooms(RoomStatus status) {
        return roomRepository.findAll()
                .stream()
                .filter(room -> room.getStatus() == status)
                .count();
    }
}