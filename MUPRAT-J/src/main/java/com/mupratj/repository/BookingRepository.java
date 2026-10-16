package com.mupratj.repository;

import com.mupratj.model.Booking;
import com.mupratj.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingRepository {

    public void save(Booking booking) {
        String sql = """
                INSERT INTO bookings (
                    bill_number, guest_name, room_type, room_number, nights,
                    extras, amount_paid, notes, created_by, created_at,
                    checked_out, deleted, deleted_by, deleted_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, booking.getBillNumber());
            statement.setString(2, booking.getGuestName());
            statement.setString(3, booking.getRoomType().name());
            statement.setString(4, booking.getRoomNumber());
            statement.setInt(5, booking.getNights());
            statement.setDouble(6, booking.getExtras());
            statement.setDouble(7, booking.getAmountPaid());
            statement.setString(8, booking.getNotes());
            statement.setString(9, booking.getCreatedBy());
            statement.setString(10, booking.getCreatedAt().toString());
            statement.setInt(11, booking.isCheckedOut() ? 1 : 0);
            statement.setInt(12, booking.isDeleted() ? 1 : 0);
            statement.setString(13, booking.getDeletedBy());
            statement.setString(14, booking.getDeletedAt() == null
                    ? null
                    : booking.getDeletedAt().toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save booking.", exception);
        }
    }

    public List<Booking> findActiveVisibleTo(User user) {
        if (user == null) {
            return List.of();
        }

        String sql = """
                SELECT * FROM bookings
                WHERE deleted = 0
                """ + (user.isAdmin() ? "" : " AND created_by = ? ")
                + " ORDER BY created_at DESC";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (!user.isAdmin()) {
                statement.setString(1, user.getUsername());
            }

            return readBookings(statement);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load bookings.", exception);
        }
    }

    public List<Booking> findDeletedForAdmin(User user) {
        if (user == null || !user.isAdmin()) {
            return List.of();
        }

        String sql = """
                SELECT * FROM bookings
                WHERE deleted = 1
                ORDER BY deleted_at DESC
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            return readBookings(statement);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load deleted receipts.", exception);
        }
    }

    public Optional<Booking> findByBillNumber(String billNumber) {
        String sql = """
                SELECT * FROM bookings
                WHERE bill_number = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, billNumber);

            List<Booking> bookings = readBookings(statement);
            return bookings.stream().findFirst();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find booking.", exception);
        }
    }

    public boolean markDeleted(
            String billNumber,
            String deletedBy,
            LocalDateTime deletedAt
    ) {
        String sql = """
                UPDATE bookings
                SET deleted = 1, deleted_by = ?, deleted_at = ?
                WHERE bill_number = ? AND deleted = 0
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, deletedBy);
            statement.setString(2, deletedAt.toString());
            statement.setString(3, billNumber);

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not archive receipt.", exception);
        }
    }

    public boolean restore(String billNumber) {
        String sql = """
                UPDATE bookings
                SET deleted = 0, deleted_by = NULL, deleted_at = NULL
                WHERE bill_number = ? AND deleted = 1
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, billNumber);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not restore receipt.", exception);
        }
    }

    private List<Booking> readBookings(PreparedStatement statement)
            throws SQLException {
        List<Booking> bookings = new ArrayList<>();

        try (ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                LocalDateTime createdAt =
                        LocalDateTime.parse(result.getString("created_at"));

                Booking booking = new Booking(
                        result.getString("bill_number"),
                        result.getString("guest_name"),
                        com.mupratj.model.RoomType.valueOf(
                                result.getString("room_type")
                        ),
                        result.getString("room_number"),
                        result.getInt("nights"),
                        result.getDouble("extras"),
                        result.getDouble("amount_paid"),
                        result.getString("notes"),
                        result.getString("created_by"),
                        createdAt
                );

                if (result.getInt("checked_out") == 1) {
                    booking.checkOut();
                }

                if (result.getInt("deleted") == 1) {
                    String deletedAtText = result.getString("deleted_at");
                    LocalDateTime deletedAt = deletedAtText == null
                            ? null
                            : LocalDateTime.parse(deletedAtText);

                    booking.markDeleted(
                            result.getString("deleted_by"),
                            deletedAt
                    );
                }

                bookings.add(booking);
            }

        }

        return bookings;
    }
    public boolean updateCheckedOut(String billNumber, boolean checkedOut) {
        String sql = """
            UPDATE bookings
            SET checked_out = ?
            WHERE bill_number = ? AND deleted = 0
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, checkedOut ? 1 : 0);
            statement.setString(2, billNumber);

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save check-out status.", exception);
        }
    }

}