package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.Room;
import com.mupratj.model.RoomType;
import com.mupratj.model.User;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.function.Consumer;

public class BookingView {
    private final BorderPane root = new BorderPane();

    public BookingView(
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

        root.setTop(new TopNavigationView(
                currentUser,
                themeController,
                onNavigate,
                onLogout
        ).getRoot());

        Label title = new Label("New guest booking");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label description = new Label(
                "Choose a room type to see the rooms that are currently available."
        );
        description.setWrapText(true);

        TextField guestName = new TextField();
        guestName.setPromptText("Guest full name");

        ComboBox<RoomType> roomType = new ComboBox<>();
        roomType.getItems().setAll(RoomType.values());
        roomType.setPromptText("Select room type");
        roomType.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Room> roomNumber = new ComboBox<>();
        roomNumber.setPromptText("Select available room");
        roomNumber.setMaxWidth(Double.MAX_VALUE);
        roomNumber.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room room) {
                return room == null ? "" : "Room " + room.getRoomNumber();
            }

            @Override
            public Room fromString(String text) {
                return null;
            }
        });

        roomType.setOnAction(event -> {
            roomNumber.getItems().setAll(
                    bookingController.getAvailableRooms(roomType.getValue())
            );

            if (roomNumber.getItems().isEmpty()) {
                roomNumber.setPromptText("No rooms of this type are available");
            } else {
                roomNumber.setPromptText("Select available room");
                roomNumber.getSelectionModel().selectFirst();
            }
        });

        Spinner<Integer> nights = new Spinner<>(1, 365, 1);
        nights.setEditable(true);

        TextField extras = new TextField("0");
        extras.setPromptText("Extras in K");

        TextField amountPaid = new TextField("0");
        amountPaid.setPromptText("Amount paid in K");

        TextArea notes = new TextArea();
        notes.setPromptText("Additional notes");
        notes.setPrefRowCount(3);
        notes.setWrapText(true);

        Label total = new Label("Estimated total: K0.00");
        total.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Runnable updateTotal = () -> {
            RoomType selectedType = roomType.getValue();
            if (selectedType == null) {
                total.setText("Estimated total: K0.00");
                return;
            }

            try {
                int stay = nights.getValue();
                double roomCharge = selectedType.getNightlyRate() * stay;
                double discount = stay >= 5 ? roomCharge * 0.05 : 0.0;
                double extraCharge = parseMoney(extras.getText());
                total.setText(String.format(
                        "Estimated total: K%.2f",
                        roomCharge - discount + extraCharge
                ));
            } catch (RuntimeException exception) {
                total.setText("Estimated total: check the entered amounts");
            }
        };

        roomType.setOnAction(event -> {
            roomNumber.getItems().setAll(
                    bookingController.getAvailableRooms(roomType.getValue())
            );
            roomNumber.setPromptText(roomNumber.getItems().isEmpty()
                    ? "No rooms of this type are available"
                    : "Select available room");
            if (!roomNumber.getItems().isEmpty()) {
                roomNumber.getSelectionModel().selectFirst();
            }
            updateTotal.run();
        });
        nights.valueProperty().addListener((obs, oldValue, newValue) -> updateTotal.run());
        extras.textProperty().addListener((obs, oldValue, newValue) -> updateTotal.run());

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        form.add(new Label("Guest name"), 0, 0);
        form.add(guestName, 1, 0);
        form.add(new Label("Room type"), 0, 1);
        form.add(roomType, 1, 1);
        form.add(new Label("Available room"), 0, 2);
        form.add(roomNumber, 1, 2);
        form.add(new Label("Nights"), 0, 3);
        form.add(nights, 1, 3);
        form.add(new Label("Extras"), 0, 4);
        form.add(extras, 1, 4);
        form.add(new Label("Amount paid"), 0, 5);
        form.add(amountPaid, 1, 5);
        form.add(new Label("Notes"), 0, 6);
        form.add(notes, 1, 6);

        Button save = new Button("Save booking and occupy room");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setPrefHeight(44);
        save.setStyle(
                "-fx-background-color: #D4AF37; -fx-text-fill: #101827; "
                        + "-fx-font-weight: bold; -fx-background-radius: 9;"
        );

        save.setOnAction(event -> {
            try {
                if (roomNumber.getValue() == null) {
                    throw new IllegalArgumentException("Select an available room.");
                }

                Booking booking = bookingController.createBooking(
                        guestName.getText(),
                        roomType.getValue(),
                        roomNumber.getValue().getRoomNumber(),
                        nights.getValue(),
                        parseMoney(extras.getText()),
                        parseMoney(amountPaid.getText()),
                        notes.getText()
                );

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        "Booking " + booking.getBillNumber()
                                + " saved for Room " + booking.getRoomNumber() + "."
                );
                alert.setHeaderText("Booking saved");
                alert.showAndWait();

                guestName.clear();
                roomType.setValue(null);
                roomNumber.getItems().clear();
                nights.getValueFactory().setValue(1);
                extras.setText("0");
                amountPaid.setText("0");
                notes.clear();
                updateTotal.run();
            } catch (RuntimeException exception) {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        exception.getMessage()
                );
                alert.setHeaderText("Booking could not be saved");
                alert.showAndWait();
            }
        });

        VBox card = new VBox(16, title, description, form, total, save);
        card.setPadding(new Insets(26));
        card.setMaxWidth(760);
        card.setStyle(dark
                ? "-fx-background-color: #1C293A; -fx-background-radius: 18;"
                : "-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 18;");

        VBox page = new VBox(card);
        page.setPadding(new Insets(28));
        page.setStyle(dark
                ? "-fx-background-color: #101827;"
                : "-fx-background-color: #F1F4F8;");
        root.setCenter(page);
    }

    private double parseMoney(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return Double.parseDouble(text.trim());
    }

    public Parent getRoot() {
        return root;
    }
}