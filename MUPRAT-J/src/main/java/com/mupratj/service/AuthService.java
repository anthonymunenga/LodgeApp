package com.mupratj.service;

import com.mupratj.model.User;
import com.mupratj.model.UserRole;
import com.mupratj.repository.UserRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class AuthService {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        createDefaultAdminIfNeeded();
    }

    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        return userRepository.findByUsername(username.trim())
                .filter(user -> verifyPassword(password, user.getPasswordHash()));
    }

    public boolean register(
            String fullName,
            String username,
            String password,
            String phone
    ) {
        if (fullName == null || fullName.isBlank()
                || username == null || username.isBlank()
                || phone == null || phone.isBlank()
                || password == null || password.length() < 8
                || userRepository.usernameExists(username.trim())) {
            return false;
        }

        User user = new User(
                fullName.trim(),
                username.trim(),
                hashPassword(password),
                phone.trim(),
                UserRole.USER
        );

        userRepository.save(user);
        return true;
    }

    private void createDefaultAdminIfNeeded() {
        boolean adminExists = userRepository.findAll()
                .stream()
                .anyMatch(User::isAdmin);

        if (!adminExists) {
            userRepository.save(new User(
                    "System Administrator",
                    "admin",
                    hashPassword("Admin@12345"),
                    "Not provided",
                    UserRole.ADMIN
            ));
        }
    }

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            byte[] hash = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            spec.clearPassword();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt) + ":"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not secure password.", exception);
        }
    }

    private boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            byte[] actualHash = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            spec.clearPassword();
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception exception) {
            return false;
        }
    }
}