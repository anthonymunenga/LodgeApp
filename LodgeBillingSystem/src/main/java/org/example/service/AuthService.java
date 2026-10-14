package org.example.service;

public class AuthService {

    public static boolean authenticate(String username, String password) {
        // Simple hardcoded login for demonstration
        return "admin".equals(username) && "1234".equals(password);
    }
}