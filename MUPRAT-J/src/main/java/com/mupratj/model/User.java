package com.mupratj.model;

public class User {
    private final String fullName;
    private final String username;
    private final String passwordHash;
    private final String phone;
    private final UserRole role;

    public User(
            String fullName,
            String username,
            String passwordHash,
            String phone,
            UserRole role
    ) {
        this.fullName = fullName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}