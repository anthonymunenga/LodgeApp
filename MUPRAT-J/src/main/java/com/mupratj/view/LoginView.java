package com.mupratj.view;

import com.mupratj.controller.LoginController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.Consumer;

public class LoginView {
    private final StackPane root = new StackPane();
    private final VBox card = new VBox(14);
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField visiblePasswordField = new TextField();
    private final Button showPasswordButton = new Button("Show");
    private final Button themeButton = new Button();
    private final Label message = new Label();

    public LoginView(
            LoginController loginController,
            ThemeController themeController,
            Consumer<User> onLogin,
            Runnable onRegister
    ) {
        root.setMinSize(900, 600);

        Pane glowLayer = new Pane();
        Circle goldGlow = new Circle(130, Color.rgb(212, 175, 55, 0.35));
        goldGlow.setLayoutX(180);
        goldGlow.setLayoutY(150);
        goldGlow.setEffect(new GaussianBlur(65));

        Circle blueGlow = new Circle(160, Color.rgb(69, 103, 160, 0.32));
        blueGlow.setLayoutX(760);
        blueGlow.setLayoutY(460);
        blueGlow.setEffect(new GaussianBlur(75));

        glowLayer.getChildren().addAll(goldGlow, blueGlow);

        Label brand = new Label("MUPRAT-J");
        brand.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");

        Label subtitle = new Label("LODGE MANAGEMENT");
        subtitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #AAB4C3;");

        Label title = new Label("Welcome back");
        title.setStyle("-fx-font-size: 27px; -fx-font-weight: bold;");

        Label description = new Label("Sign in to manage bookings, rooms, and guest accounts.");
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #7B8798;");

        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(44);

        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(44);

        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefHeight(44);
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        showPasswordButton.setOnAction(event -> {
            boolean show = passwordField.isVisible();
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);
            visiblePasswordField.setVisible(show);
            visiblePasswordField.setManaged(show);
            showPasswordButton.setText(show ? "Hide" : "Show");
        });

        HBox passwordRow = new HBox(8, passwordField, visiblePasswordField, showPasswordButton);
        passwordField.prefWidthProperty().bind(passwordRow.widthProperty().subtract(75));
        visiblePasswordField.prefWidthProperty().bind(passwordRow.widthProperty().subtract(75));

        Button signIn = new Button("SIGN IN");
        signIn.setMaxWidth(Double.MAX_VALUE);
        signIn.setPrefHeight(46);
        signIn.setStyle("-fx-background-color: #D4AF37; -fx-text-fill: #101827; -fx-font-weight: bold; -fx-background-radius: 10;");

        Button register = new Button("Create an account");
        register.setMaxWidth(Double.MAX_VALUE);
        register.setStyle("-fx-background-color: transparent; -fx-text-fill: #D4AF37; -fx-font-weight: bold;");

        message.setStyle("-fx-text-fill: #E05252;");
        message.setWrapText(true);

        signIn.setOnAction(event -> {
            if (loginController.login(usernameField.getText(), passwordField.getText())) {
                message.setText("");
                onLogin.accept(loginController.getCurrentUser());
            } else {
                message.setText("Username or password is incorrect.");
            }
        });

        passwordField.setOnAction(event -> signIn.fire());
        register.setOnAction(event -> onRegister.run());

        themeButton.setOnAction(event -> {
            boolean dark = themeController.toggleTheme();
            applyTheme(dark);
        });

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(34));
        card.setMaxWidth(430);
        card.getChildren().addAll(
                title,
                description,
                new Label("Username"),
                usernameField,
                new Label("Password"),
                passwordRow,
                signIn,
                message,
                register
        );

        VBox branding = new VBox(8, brand, subtitle);
        branding.setAlignment(Pos.CENTER_LEFT);
        branding.setMaxWidth(320);

        HBox content = new HBox(70, branding, card);
        content.setAlignment(Pos.CENTER);

        StackPane.setAlignment(themeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(themeButton, new Insets(18));

        root.getChildren().addAll(glowLayer, content, themeButton);
        applyTheme(themeController.isDarkMode());
    }

    private void applyTheme(boolean dark) {
        root.setStyle(dark
                ? "-fx-background-color: #101827;"
                : "-fx-background-color: #EEF2F7;");

        card.setStyle(dark
                ? "-fx-background-color: rgba(28, 41, 58, 0.92); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.38), 24, 0.18, 0, 8);"
                : "-fx-background-color: rgba(255,255,255,0.92); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(16,24,39,0.16), 24, 0.18, 0, 8);");

        String inputStyle = dark
                ? "-fx-background-color: #172234; -fx-text-fill: #F3F6FA; -fx-prompt-text-fill: #8492A6; -fx-background-radius: 9; -fx-padding: 11;"
                : "-fx-background-color: #F3F5F8; -fx-text-fill: #172234; -fx-prompt-text-fill: #8492A6; -fx-background-radius: 9; -fx-padding: 11;";

        usernameField.setStyle(inputStyle);
        passwordField.setStyle(inputStyle);
        visiblePasswordField.setStyle(inputStyle);
        showPasswordButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #D4AF37; -fx-font-weight: bold;");

        card.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> label.setStyle(dark
                        ? "-fx-text-fill: #EDF2F7;"
                        : "-fx-text-fill: #172234;"));

        themeButton.setText(dark ? "Switch to Light" : "Switch to Dark");
        themeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #D4AF37; -fx-font-weight: bold;");
    }

    public Parent getRoot() {
        return root;
    }
}