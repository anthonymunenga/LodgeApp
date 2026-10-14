package org.example.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {

    private static final String DB_URL = "jdbc:sqlite:lodge.db";

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS receipts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "guest_name TEXT NOT NULL, " +
                "room_type TEXT NOT NULL, " +
                "nightly_rate REAL NOT NULL, " +
                "nights INTEGER NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error initializing receipts table: " + e.getMessage());
        }
    }

    public static void initializeRoomsTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS rooms (" +
                "room_number TEXT PRIMARY KEY, " +
                "room_type TEXT NOT NULL, " +
                "status TEXT NOT NULL" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);

            // Populate default rooms if table is empty
            String checkSql = "SELECT COUNT(*) FROM rooms";
            ResultSet rs = stmt.executeQuery(checkSql);
            if (rs.next() && rs.getInt(1) == 0) {
                String[] defaultRooms = {
                        "INSERT INTO rooms VALUES ('101', 'Single Room', 'AVAILABLE')",
                        "INSERT INTO rooms VALUES ('102', 'Single Room', 'AVAILABLE')",
                        "INSERT INTO rooms VALUES ('201', 'Double Room', 'AVAILABLE')",
                        "INSERT INTO rooms VALUES ('202', 'Double Room', 'AVAILABLE')",
                        "INSERT INTO rooms VALUES ('301', 'Executive Suite', 'AVAILABLE')"
                };
                for (String insertSql : defaultRooms) {
                    stmt.execute(insertSql);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing rooms table: " + e.getMessage());
        }
    }

    public static void saveReceipt(Bill bill) {
        String insertSql = "INSERT INTO receipts (guest_name, room_type, nightly_rate, nights, total_amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setString(1, bill.getGuestName());
            pstmt.setString(2, bill.getRoomType());
            pstmt.setDouble(3, bill.getNightlyRate());
            pstmt.setInt(4, bill.getNights());
            pstmt.setDouble(5, bill.getTotalAmount());
            pstmt.executeUpdate();

            // Update an available room of this type to OCCUPIED
            updateRoomStatusForType(conn, bill.getRoomType(), "OCCUPIED");

        } catch (SQLException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
    }

    private static void updateRoomStatusForType(Connection conn, String roomType, String status) {
        String findSql = "SELECT room_number FROM rooms WHERE room_type = ? AND status = 'AVAILABLE' LIMIT 1";
        String updateSql = "UPDATE rooms SET status = ? WHERE room_number = ?";

        try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setString(1, roomType);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                String roomNum = rs.getString("room_number");
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, status);
                    updateStmt.setString(2, roomNum);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating room status: " + e.getMessage());
        }
    }

    // New method to vacate a room when a guest checks out
    public static void vacateRoom(String roomNumber) {
        String sql = "UPDATE rooms SET status = 'AVAILABLE' WHERE room_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            pstmt.executeUpdate();
            System.out.println("Room " + roomNumber + " is now AVAILABLE.");
        } catch (SQLException e) {
            System.err.println("Error vacating room: " + e.getMessage());
        }
    }

    public static Map<String, String> getRoomStatuses() {
        Map<String, String> statuses = new HashMap<>();
        String sql = "SELECT room_number, status FROM rooms";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                statuses.put(rs.getString("room_number"), rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching room statuses: " + e.getMessage());
        }
        return statuses;
    }

    public static String getFinancialSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== FINANCIAL SUMMARY REPORT ===\n\n");

        String query = "SELECT * FROM receipts";
        double totalRevenue = 0.0;
        int totalCount = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                totalCount++;
                String guest = rs.getString("guest_name");
                String room = rs.getString("room_type");
                double total = rs.getDouble("total_amount");
                totalRevenue += total;

                report.append(String.format("Guest: %s | Room: %s | Total: K %.2f\n", guest, room, total));
            }

        } catch (SQLException e) {
            return "Error generating report: " + e.getMessage();
        }

        report.append("\n----------------------------------------\n");
        report.append(String.format("Total Receipts: %d\n", totalCount));
        report.append(String.format("Total Revenue: K %.2f\n", totalRevenue));

        return report.toString();
    }

    public static List<String> searchReceipts(String keyword) {
        List<String> results = new ArrayList<>();
        String query = "SELECT * FROM receipts WHERE guest_name LIKE ? OR room_type LIKE ? ORDER BY timestamp DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + (keyword == null ? "" : keyword) + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String entry = String.format("ID: %d | Guest: %s | Room: %s | Total: K %.2f | Date: %s",
                            rs.getInt("id"),
                            rs.getString("guest_name"),
                            rs.getString("room_type"),
                            rs.getDouble("total_amount"),
                            rs.getString("timestamp"));
                    results.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching receipts: " + e.getMessage());
        }

        return results;
    }
}