package com.mupratj.repository;

import com.mupratj.model.Room;
import com.mupratj.model.RoomStatus;
import com.mupratj.model.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository {

    public List<Room> findAll() {
        String sql = """
                SELECT room_number, room_type, status
                FROM rooms
                ORDER BY room_number
                """;

        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                rooms.add(mapRoom(result));
            }

            return rooms;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load rooms.", exception);
        }
    }

    public List<Room> findAvailableByType(RoomType roomType) {
        String sql = """
                SELECT room_number, room_type, status
                FROM rooms
                WHERE room_type = ? AND status = 'AVAILABLE'
                ORDER BY room_number
                """;

        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, roomType.name());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rooms.add(mapRoom(result));
                }
            }

            return rooms;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find available rooms.", exception);
        }
    }

    public Optional<Room> findByNumber(String roomNumber) {
        String sql = """
                SELECT room_number, room_type, status
                FROM rooms
                WHERE room_number = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, roomNumber);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? Optional.of(mapRoom(result))
                        : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find room.", exception);
        }
    }

    public boolean occupy(String roomNumber) {
        return updateStatus(roomNumber, RoomStatus.OCCUPIED, RoomStatus.AVAILABLE);
    }

    public boolean checkOut(String roomNumber) {
        return updateStatus(roomNumber, RoomStatus.AVAILABLE, RoomStatus.OCCUPIED);
    }

    public boolean markForMaintenance(String roomNumber) {
        return updateStatus(roomNumber, RoomStatus.MAINTENANCE, RoomStatus.AVAILABLE);
    }

    private boolean updateStatus(
            String roomNumber,
            RoomStatus newStatus,
            RoomStatus requiredCurrentStatus
    ) {
        String sql = """
                UPDATE rooms
                SET status = ?
                WHERE room_number = ? AND status = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newStatus.name());
            statement.setString(2, roomNumber);
            statement.setString(3, requiredCurrentStatus.name());

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not update room status.", exception);
        }
    }

    private Room mapRoom(ResultSet result) throws SQLException {
        return new Room(
                result.getString("room_number"),
                RoomType.valueOf(result.getString("room_type")),
                RoomStatus.valueOf(result.getString("status"))
        );
    }
}