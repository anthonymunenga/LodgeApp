package com.mupratj.repository;

import com.mupratj.model.User;
import com.mupratj.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT full_name, username, password_hash, phone, role
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                User user = new User(
                        result.getString("full_name"),
                        result.getString("username"),
                        result.getString("password_hash"),
                        result.getString("phone"),
                        UserRole.valueOf(result.getString("role"))
                );

                return Optional.of(user);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find user.", exception);
        }
    }

    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    public void save(User user) {
        String sql = """
                INSERT INTO users (username, full_name, password_hash, phone, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getRole().name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save user.", exception);
        }
    }

    public List<User> findAll() {
        String sql = """
                SELECT full_name, username, password_hash, phone, role
                FROM users
                ORDER BY full_name
                """;

        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                users.add(new User(
                        result.getString("full_name"),
                        result.getString("username"),
                        result.getString("password_hash"),
                        result.getString("phone"),
                        UserRole.valueOf(result.getString("role"))
                ));
            }

            return users;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load users.", exception);
        }
    }
}