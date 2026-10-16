package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.function.Consumer;

public class ReceiptView {
    private final BorderPane root = new BorderPane();

    public ReceiptView(
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

        Label title = new Label("Guest receipt");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        ComboBox<Booking> bookingPicker = new ComboBox<>();
        bookingPicker.getItems().setAll(bookingController.getVisibleBookings());
        bookingPicker.setPromptText("Choose a bill to view its receipt");
        bookingPicker.setMaxWidth(650);
        bookingPicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(Booking booking) {
                return booking == null
                        ? ""
                        : booking.getBillNumber() + " — " + booking.getGuestName();
            }

            @Override
            public Booking fromString(String text) {
                return null;
            }
        });

        Label brand = new Label("MUPRAT-J");
        brand.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");

        Label receiptTitle = new Label("OFFICIAL GUEST RECEIPT");
        receiptTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label billNumber = new Label("Bill: —");
        Label guest = new Label("Guest: —");
        Label room = new Label("Room: —");
        Label stay = new Label("Stay: —");
        Label date = new Label("Date: —");
        Label roomCharge = new Label("Room charge: K0.00");
        Label discount = new Label("Discount: K0.00");
        Label extras = new Label("Extras: K0.00");
        Label total = new Label("TOTAL: K0.00");
        Label paid = new Label("Amount paid: K0.00");
        Label balance = new Label("Balance: K0.00");

        total.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        balance.setStyle("-fx-font-weight: bold;");

        TextArea notes = new TextArea();
        notes.setEditable(false);
        notes.setWrapText(true);
        notes.setPrefRowCount(3);
        notes.setPromptText("No additional notes");

        bookingPicker.setOnAction(event -> {
            Booking selected = bookingPicker.getValue();
            if (selected == null) {
                return;
            }

            billNumber.setText("Bill: " + selected.getBillNumber());
            guest.setText("Guest: " + selected.getGuestName());
            room.setText("Room: " + selected.getRoomType().getDisplayName()
                    + " — " + selected.getRoomNumber());
            stay.setText("Stay: " + selected.getNights() + " night(s)");
            date.setText("Date: " + selected.getCreatedAt());
            roomCharge.setText(String.format(
                    "Room charge: K%.2f", selected.getRoomCharge()));
            discount.setText(String.format(
                    "Discount: K%.2f", selected.getDiscount()));
            extras.setText(String.format(
                    "Extras: K%.2f", selected.getExtras()));
            total.setText(String.format(
                    "TOTAL: K%.2f", selected.getTotal()));
            paid.setText(String.format(
                    "Amount paid: K%.2f", selected.getAmountPaid()));
            balance.setText(String.format(
                    "Balance: K%.2f", selected.getBalance()));
            notes.setText(selected.getNotes());
        });

        VBox receipt = new VBox(
                12,
                brand,
                receiptTitle,
                new Label("________________________________________"),
                billNumber,
                guest,
                room,
                stay,
                date,
                new Label("________________________________________"),
                roomCharge,
                discount,
                extras,
                total,
                paid,
                balance,
                new Label("Notes"),
                notes
        );
        receipt.setPadding(new Insets(28));
        receipt.setMaxWidth(650);
        receipt.setStyle(dark
                ? "-fx-background-color: rgba(28,41,58,0.96); -fx-background-radius: 18;"
                : "-fx-background-color: rgba(255,255,255,0.96); -fx-background-radius: 18;");

        VBox content = new VBox(18, title, bookingPicker, receipt);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(26));
        root.setCenter(content);
    }

    public Parent getRoot() {
        return root;
    }
}