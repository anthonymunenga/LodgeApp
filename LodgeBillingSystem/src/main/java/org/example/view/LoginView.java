package org.example.view;

import org.example.service.AuthService;
import org.example.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginView {

    public static Scene createScene(Main mainApp) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F4F6F8;");

        Label titleLabel = new Label("Lodge Management Login");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);
        usernameField.setStyle("-fx-padding: 8;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-padding: 8;");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.valueOf("#EF4444"));

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            // Using the AuthService class to authenticate
            if (AuthService.authenticate(user, pass)) {
                if (mainApp != null) {
                    mainApp.showDashboard();
                }
            } else {
                errorLabel.setText("Invalid username or password!");
            }
        });

        layout.getChildren().addAll(titleLabel, usernameField, passwordField, loginBtn, errorLabel);

        return new Scene(layout, 400, 350);
    }
}