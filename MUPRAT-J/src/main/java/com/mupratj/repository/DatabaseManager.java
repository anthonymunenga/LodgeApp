package com.mupratj.repository;

import com.mupratj.model.RoomType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:mupratj.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initialize() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        username TEXT PRIMARY KEY,
                        full_name TEXT NOT NULL,
                        password_hash TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        role TEXT NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS rooms (
                        room_number TEXT PRIMARY KEY,
                        room_type TEXT NOT NULL,
                        status TEXT NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS bookings (
                        bill_number TEXT PRIMARY KEY,
                        guest_name TEXT NOT NULL,
                        room_type TEXT NOT NULL,
                        room_number TEXT NOT NULL,
                        nights INTEGER NOT NULL,
                        extras REAL NOT NULL,
                        amount_paid REAL NOT NULL,
                        notes TEXT,
                        created_by TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        checked_out INTEGER NOT NULL DEFAULT 0,
                        deleted INTEGER NOT NULL DEFAULT 0,
                        deleted_by TEXT,
                        deleted_at TEXT
                    )
                    """);

            seedRooms(connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not initialize MUPRAT-J database.", exception);
        }
    }

    private static void seedRooms(Connection connection) throws SQLException {
        String insertSql = """
                INSERT OR IGNORE INTO rooms (room_number, room_type, status)
                VALUES (?, ?, 'AVAILABLE')
                """;

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (RoomType type : RoomType.values()) {
                int prefix = switch (type) {
                    case STANDARD -> 1;
                    case FAMILY -> 2;
                    case PREMIUM -> 3;
                };

                for (int number = 1; number <= type.getRoomCount(); number++) {
                    String roomNumber = String.format("%d%02d", prefix, number);
                    statement.setString(1, roomNumber);
                    statement.setString(2, type.name());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }
}