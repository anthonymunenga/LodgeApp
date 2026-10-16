package com.mupratj.controller;

import com.mupratj.model.User;
import com.mupratj.service.AuthService;

import java.util.Optional;

public class LoginController {
    private final AuthService authService;
    private User currentUser;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String username, String password) {
        Optional<User> user = authService.authenticate(username, password);

        if (user.isEmpty()) {
            currentUser = null;
            return false;
        }

        currentUser = user.get();
        return true;
    }

    public boolean register(
            String fullName,
            String username,
            String password,
            String phone
    ) {
        return authService.register(fullName, username, password, phone);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}