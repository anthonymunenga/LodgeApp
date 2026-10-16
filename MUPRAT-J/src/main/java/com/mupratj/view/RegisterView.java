package com.mupratj.view;

import com.mupratj.controller.LoginController;
import com.mupratj.controller.ThemeController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RegisterView {
    private final StackPane root = new StackPane();

    public RegisterView(
            LoginController loginController,
            ThemeController themeController,
            Runnable onBack
    ) {
        VBox card = new VBox(13);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(32));
        card.setMaxWidth(440);

        Label title = new Label("Create an account");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label(
                "New accounts are created with regular-user permissions."
        );
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-text-fill: #778397;");

        TextField fullName = new TextField();
        fullName.setPromptText("Full name");

        TextField username = new TextField();
        username.setPromptText("Username");

        TextField phone = new TextField();
        phone.setPromptText("Phone number");

        PasswordField password = new PasswordField();
        password.setPromptText("Password (at least 8 characters)");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm password");

        Button createAccount = new Button("CREATE ACCOUNT");
        createAccount.setMaxWidth(Double.MAX_VALUE);
        createAccount.setPrefHeight(44);
        createAccount.setStyle(
                "-fx-background-color: #D4AF37; -fx-text-fill: #101827; "
                        + "-fx-font-weight: bold; -fx-background-radius: 9;"
        );

        Button back = new Button("Back to sign in");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #D4AF37; "
                        + "-fx-font-weight: bold;"
        );
        back.setOnAction(event -> onBack.run());

        createAccount.setOnAction(event -> {
            if (!password.getText().equals(confirmPassword.getText())) {
                showMessage(Alert.AlertType.ERROR, "Passwords do not match.");
                return;
            }

            boolean registered = loginController.register(
                    fullName.getText(),
                    username.getText(),
                    password.getText(),
                    phone.getText()
            );

            if (!registered) {
                showMessage(
                        Alert.AlertType.ERROR,
                        "Please complete every field, use a password with at least 8 characters, "
                                + "and choose a username that has not been used."
                );
                return;
            }

            showMessage(
                    Alert.AlertType.INFORMATION,
                    "Your account has been created. You can now sign in."
            );
            onBack.run();
        });

        card.getChildren().addAll(
                title,
                subtitle,
                new Label("Full name"),
                fullName,
                new Label("Username"),
                username,
                new Label("Phone number"),
                phone,
                new Label("Password"),
                password,
                confirmPassword,
                createAccount,
                back
        );

        root.setMinSize(700, 600);
        root.getChildren().add(card);
        applyTheme(themeController.isDarkMode(), card, fullName, username, phone, password, confirmPassword);
    }

    private void applyTheme(
            boolean dark,
            VBox card,
            TextField fullName,
            TextField username,
            TextField phone,
            PasswordField password,
            PasswordField confirmPassword
    ) {
        root.setStyle(dark
                ? "-fx-background-color: #101827;"
                : "-fx-background-color: #EEF2F7;");

        card.setStyle(dark
                ? "-fx-background-color: #1C293A; -fx-background-radius: 20; "
                  + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 22, 0.16, 0, 8);"
                : "-fx-background-color: rgba(255,255,255,0.94); -fx-background-radius: 20; "
                  + "-fx-effect: dropshadow(gaussian, rgba(16,24,39,0.16), 22, 0.16, 0, 8);");

        String fieldStyle = dark
                ? "-fx-background-color: #172234; -fx-text-fill: #F3F6FA; "
                  + "-fx-prompt-text-fill: #8492A6; -fx-background-radius: 9; -fx-padding: 11;"
                : "-fx-background-color: #F3F5F8; -fx-text-fill: #172234; "
                  + "-fx-prompt-text-fill: #8492A6; -fx-background-radius: 9; -fx-padding: 11;";

        fullName.setStyle(fieldStyle);
        username.setStyle(fieldStyle);
        phone.setStyle(fieldStyle);
        password.setStyle(fieldStyle);
        confirmPassword.setStyle(fieldStyle);
    }

    private void showMessage(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.setTitle("MUPRAT-J");
        alert.showAndWait();
    }

    public Parent getRoot() {
        return root;
    }
}