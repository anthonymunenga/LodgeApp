package com.mupratj.service;

import com.mupratj.model.Booking;
import com.mupratj.model.Room;
import com.mupratj.model.RoomType;
import com.mupratj.model.User;
import com.mupratj.repository.BookingRepository;
import com.mupratj.repository.RoomRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public List<Booking> getBookingsVisibleTo(User user) {
        return bookingRepository.findActiveVisibleTo(user);
    }

    public List<Booking> getDeletedBookings(User user) {
        return bookingRepository.findDeletedForAdmin(user);
    }

    public List<Room> getAvailableRooms(RoomType roomType) {
        return roomRepository.findAvailableByType(roomType);
    }

    public Booking createBooking(
            String guestName,
            RoomType roomType,
            String roomNumber,
            int nights,
            double extras,
            double amountPaid,
            String notes,
            User currentUser
    ) {
        if (currentUser == null) {
            throw new IllegalArgumentException("You must be logged in to create a booking.");
        }
        if (guestName == null || guestName.isBlank()) {
            throw new IllegalArgumentException("Enter the guest's name.");
        }
        if (roomType == null || roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Choose an available room.");
        }
        if (nights < 1) {
            throw new IllegalArgumentException("The stay must be at least one night.");
        }
        if (extras < 0 || amountPaid < 0) {
            throw new IllegalArgumentException("Extras and payment cannot be negative.");
        }

        Room room = roomRepository.findByNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("That room does not exist."));

        if (room.getRoomType() != roomType) {
            throw new IllegalArgumentException("Choose a room matching the selected room type.");
        }

        if (!roomRepository.occupy(roomNumber)) {
            throw new IllegalArgumentException("That room is no longer available. Choose another room.");
        }

        String billNumber = "MJP-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        Booking booking = new Booking(
                billNumber,
                guestName.trim(),
                roomType,
                roomNumber,
                nights,
                extras,
                amountPaid,
                notes == null ? "" : notes.trim(),
                currentUser.getUsername(),
                LocalDateTime.now()
        );

        if (amountPaid > booking.getTotal()) {
            roomRepository.checkOut(roomNumber);
            throw new IllegalArgumentException("Payment cannot be greater than the bill total.");
        }

        try {
            bookingRepository.save(booking);
            return booking;
        } catch (RuntimeException exception) {
            roomRepository.checkOut(roomNumber);
            throw exception;
        }
    }

    public boolean checkOut(Booking booking, User currentUser) {
        if (!canManage(booking, currentUser)
                || booking.isDeleted()
                || booking.isCheckedOut()) {
            return false;
        }

        if (!bookingRepository.updateCheckedOut(booking.getBillNumber(), true)) {
            return false;
        }

        if (!roomRepository.checkOut(booking.getRoomNumber())) {
            bookingRepository.updateCheckedOut(booking.getBillNumber(), false);
            return false;
        }

        booking.checkOut();
        return true;
    }

    public boolean deleteReceipt(Booking booking, User currentUser) {
        if (!canManage(booking, currentUser) || booking.isDeleted()) {
            return false;
        }

        boolean archived = bookingRepository.markDeleted(
                booking.getBillNumber(),
                currentUser.getUsername(),
                LocalDateTime.now()
        );

        if (archived) {
            booking.markDeleted(currentUser.getUsername(), LocalDateTime.now());
        }

        return archived;
    }

    public boolean restoreReceipt(Booking booking, User currentUser) {
        if (booking == null || currentUser == null
                || !currentUser.isAdmin() || !booking.isDeleted()) {
            return false;
        }

        boolean restored = bookingRepository.restore(booking.getBillNumber());

        if (restored) {
            booking.restore();
        }

        return restored;
    }

    private boolean canManage(Booking booking, User user) {
        return booking != null
                && user != null
                && (user.isAdmin()
                || user.getUsername().equals(booking.getCreatedBy()));
    }
}