package com.mupratj.view;

import com.mupratj.controller.ThemeController;
import com.mupratj.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public class TopNavigationView {
    private final HBox root = new HBox(6);

    public TopNavigationView(
            User currentUser,
            ThemeController themeController,
            Consumer<String> onNavigate,
            Runnable onLogout
    ) {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(12, 16, 12, 16));
        root.setStyle(
                "-fx-background-color: rgba(17,27,43,0.96); "
                        + "-fx-background-radius: 0 0 14 14; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.20), 14, 0.10, 0, 4);"
        );

        Label brand = new Label("MUPRAT-J");
        brand.setStyle(
                "-fx-text-fill: #D4AF37; -fx-font-size: 16px; -fx-font-weight: bold;"
        );

        root.getChildren().add(brand);
        addPageButton("Dashboard", "dashboard", onNavigate);
        addPageButton("New Booking", "booking", onNavigate);
        addPageButton("Bills", "bills", onNavigate);
        addPageButton("Receipts", "receipts", onNavigate);
        addPageButton("Rooms", "rooms", onNavigate);
        addPageButton("Financials", "financials", onNavigate);
        addPageButton("Search", "search", onNavigate);

        if (currentUser != null && currentUser.isAdmin()) {
            addPageButton("Deleted Archive", "archive", onNavigate);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        root.getChildren().add(spacer);

        Label role = new Label(
                currentUser == null || !currentUser.isAdmin()
                        ? "USER"
                        : "ADMIN"
        );
        role.setStyle(
                "-fx-text-fill: #DCE4EF; -fx-font-size: 11px; -fx-font-weight: bold; "
                        + "-fx-background-color: #334155; -fx-background-radius: 12; "
                        + "-fx-padding: 6 10;"
        );

        Button themeButton = new Button(
                themeController.isDarkMode() ? "Light mode" : "Dark mode"
        );
        styleButton(themeButton);
        themeButton.setOnAction(event -> {
            themeController.toggleTheme();
            onNavigate.accept("theme");
        });

        Button logoutButton = new Button("Log out");
        styleButton(logoutButton);
        logoutButton.setOnAction(event -> onLogout.run());

        root.getChildren().addAll(role, themeButton, logoutButton);
    }

    private void addPageButton(
            String label,
            String page,
            Consumer<String> onNavigate
    ) {
        Button button = new Button(label);
        styleButton(button);
        button.setOnAction(event -> onNavigate.accept(page));
        root.getChildren().add(button);
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #EDF2F7; "
                        + "-fx-font-weight: bold; -fx-background-radius: 8; "
                        + "-fx-padding: 8 10; -fx-cursor: hand;"
        );
    }

    public Parent getRoot() {
        return root;
    }
}