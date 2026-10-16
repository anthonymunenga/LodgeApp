package com.mupratj.controller;

import com.mupratj.model.Booking;
import com.mupratj.model.Room;
import com.mupratj.model.RoomType;
import com.mupratj.service.BookingService;

import java.util.List;

public class BookingController {
    private final BookingService bookingService;
    private final LoginController loginController;

    public BookingController(
            BookingService bookingService,
            LoginController loginController
    ) {
        this.bookingService = bookingService;
        this.loginController = loginController;
    }

    public List<Room> getAvailableRooms(RoomType roomType) {
        return bookingService.getAvailableRooms(roomType);
    }

    public Booking createBooking(
            String guestName,
            RoomType roomType,
            String roomNumber,
            int nights,
            double extras,
            double amountPaid,
            String notes
    ) {
        return bookingService.createBooking(
                guestName,
                roomType,
                roomNumber,
                nights,
                extras,
                amountPaid,
                notes,
                loginController.getCurrentUser()
        );
    }

    public List<Booking> getVisibleBookings() {
        return bookingService.getBookingsVisibleTo(
                loginController.getCurrentUser()
        );
    }

    public List<Booking> getDeletedBookings() {
        return bookingService.getDeletedBookings(
                loginController.getCurrentUser()
        );
    }

    public boolean checkOut(Booking booking) {
        return bookingService.checkOut(
                booking,
                loginController.getCurrentUser()
        );
    }

    public boolean deleteReceipt(Booking booking) {
        return bookingService.deleteReceipt(
                booking,
                loginController.getCurrentUser()
        );
    }

    public boolean restoreReceipt(Booking booking) {
        return bookingService.restoreReceipt(
                booking,
                loginController.getCurrentUser()
        );
    }
}