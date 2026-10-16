package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class DashboardView {
    private final BorderPane root = new BorderPane();

    public DashboardView(
            BookingController bookingController,
            ThemeController themeController,
            User currentUser,
            Consumer<String> onNavigate,
            Runnable onLogout
    ) {
        boolean dark = themeController.isDarkMode();
        root.setStyle(dark
                ? "-fx-background-color: #101827;"
                : "-fx-background-color: #F1F4F8;");

        TopNavigationView navigation = new TopNavigationView(
                currentUser,
                themeController,
                onNavigate,
                onLogout
        );
        root.setTop(navigation.getRoot());

        List<Booking> bookings = bookingController.getVisibleBookings();

        Label welcome = new Label(
                "Welcome, " + currentUser.getFullName()
        );
        welcome.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold; "
                        + (dark ? "-fx-text-fill: #F3F6FA;" : "-fx-text-fill: #172234;")
        );

        Label description = new Label(
                "Here is the current activity for MUPRAT-J."
        );
        description.setStyle(
                dark ? "-fx-text-fill: #AAB4C3;" : "-fx-text-fill: #667085;"
        );

        double revenue = bookings.stream()
                .mapToDouble(Booking::getTotal)
                .sum();
        double paid = bookings.stream()
                .mapToDouble(Booking::getAmountPaid)
                .sum();
        double balance = bookings.stream()
                .mapToDouble(Booking::getBalance)
                .sum();

        HBox summaryCards = new HBox(
                14,
                createCard("VISIBLE BILLS", String.valueOf(bookings.size()), dark),
                createCard("TOTAL BILLED", money(revenue), dark),
                createCard("AMOUNT PAID", money(paid), dark),
                createCard("OUTSTANDING", money(balance), dark)
        );

        TableView<Booking> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, String> billColumn = new TableColumn<>("Bill No.");
        billColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));

        TableColumn<Booking, String> guestColumn = new TableColumn<>("Guest");
        guestColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));

        TableColumn<Booking, String> roomTypeColumn = new TableColumn<>("Room Type");
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Booking, String> roomNumberColumn = new TableColumn<>("Room");
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Booking, Integer> nightsColumn = new TableColumn<>("Nights");
        nightsColumn.setCellValueFactory(new PropertyValueFactory<>("nights"));

        TableColumn<Booking, Number> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(
                        cell.getValue().getTotal()
                )
        );

        TableColumn<Booking, Number> balanceColumn = new TableColumn<>("Balance");
        balanceColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(
                        cell.getValue().getBalance()
                )
        );

        table.getColumns().addAll(
                billColumn,
                guestColumn,
                roomTypeColumn,
                roomNumberColumn,
                nightsColumn,
                totalColumn,
                balanceColumn
        );
        table.setItems(FXCollections.observableArrayList(bookings));

        Label recent = new Label("Recent bookings");
        recent.setStyle(
                "-fx-font-size: 19px; -fx-font-weight: bold; "
                        + (dark ? "-fx-text-fill: #F3F6FA;" : "-fx-text-fill: #172234;")
        );

        VBox content = new VBox(18, welcome, description, summaryCards, recent, table);
        content.setPadding(new Insets(28));
        VBox.setVgrow(table, Priority.ALWAYS);
        HBox.setHgrow(summaryCards, Priority.ALWAYS);
        root.setCenter(content);
    }

    private VBox createCard(String title, String value, boolean dark) {
        Label heading = new Label(title);
        heading.setStyle(
                "-fx-font-size: 11px; -fx-font-weight: bold; "
                        + (dark ? "-fx-text-fill: #AAB4C3;" : "-fx-text-fill: #667085;")
        );

        Label amount = new Label(value);
        amount.setStyle(
                "-fx-font-size: 21px; -fx-font-weight: bold; "
                        + (dark ? "-fx-text-fill: #F3F6FA;" : "-fx-text-fill: #172234;")
        );

        VBox card = new VBox(10, heading, amount);
        card.setPadding(new Insets(18));
        card.setMinWidth(150);
        card.setStyle(dark
                ? "-fx-background-color: rgba(28,41,58,0.92); -fx-background-radius: 14;"
                : "-fx-background-color: rgba(255,255,255,0.94); -fx-background-radius: 14;");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private String money(double amount) {
        return String.format("K%.2f", amount);
    }

    public Parent getRoot() {
        return root;
    }
}