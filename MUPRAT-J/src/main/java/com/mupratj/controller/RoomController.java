package com.mupratj.controller;

import com.mupratj.model.Room;
import com.mupratj.model.RoomStatus;
import com.mupratj.model.RoomType;
import com.mupratj.service.RoomService;

import java.util.List;

public class RoomController {
    private final RoomService roomService;
    private final LoginController loginController;

    public RoomController(
            RoomService roomService,
            LoginController loginController
    ) {
        this.roomService = roomService;
        this.loginController = loginController;
    }

    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    public List<Room> getAvailableRooms(RoomType roomType) {
        return roomService.getAvailableRooms(roomType);
    }

    public long countRooms(RoomStatus status) {
        return roomService.countRooms(status);
    }

    public boolean markForMaintenance(String roomNumber) {
        return roomService.markForMaintenance(
                roomNumber,
                loginController.getCurrentUser()
        );
    }

    public boolean isCurrentUserAdmin() {
        return loginController.getCurrentUser() != null
                && loginController.getCurrentUser().isAdmin();
    }
}