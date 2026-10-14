package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.Main;
import org.example.model.Bill;
import org.example.model.DatabaseService;

public class DashboardView {

    public static Scene createScene(Main mainApp) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F4F6F8;");

        // --- TOP HEADER ---
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1E293B;");

        Label titleLabel = new Label("Lodge Billing & Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button roomStatusBtn = new Button("Room Status");
        roomStatusBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-weight: bold;");
        roomStatusBtn.setOnAction(e -> RoomStatusView.show());

        Button reportBtn = new Button("Financial Report");
        reportBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-weight: bold;");
        reportBtn.setOnAction(e -> FinancialReportDialog.show());

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            if (mainApp != null) {
                mainApp.showLogin();
            }
        });

        header.getChildren().addAll(titleLabel, spacer, roomStatusBtn, reportBtn, logoutBtn);
        root.setTop(header);

        // --- CENTER: BILLING FORM & SEARCH ---
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(30));
        centerContent.setAlignment(Pos.TOP_CENTER);

        // Form Card Container
        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(25));
        formCard.setMaxWidth(500);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label formTitle = new Label("New Guest Billing");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        formTitle.setTextFill(Color.valueOf("#1E293B"));

        TextField guestNameField = new TextField();
        guestNameField.setPromptText("Enter guest full name");
        guestNameField.setStyle("-fx-padding: 8;");

        ComboBox<String> roomTypeCombo = new ComboBox<>();
        roomTypeCombo.getItems().addAll(Bill.ROOM_PRICES.keySet());
        roomTypeCombo.setPromptText("-- Select Room Type --");
        roomTypeCombo.setMaxWidth(Double.MAX_VALUE);
        roomTypeCombo.setStyle("-fx-padding: 5;");

        TextField nightlyRateField = new TextField();
        nightlyRateField.setPromptText("Auto-filled based on room type");
        nightlyRateField.setEditable(false);
        nightlyRateField.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-padding: 8;");

        TextField nightsField = new TextField();
        nightsField.setPromptText("Number of nights (e.g., 3)");
        nightsField.setStyle("-fx-padding: 8;");

        Label totalLabel = new Label("Total: K 0.00");
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.valueOf("#0F172A"));

        // Auto-fill price logic when room type changes
        roomTypeCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && Bill.ROOM_PRICES.containsKey(newValue)) {
                double rate = Bill.ROOM_PRICES.get(newValue);
                nightlyRateField.setText(String.format("%.2f", rate));
                calculateAndUpdateTotal(rate, nightsField, totalLabel);
            }
        });

        // Recalculate total when nights change
        nightsField.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                if (!nightlyRateField.getText().isEmpty() && !newValue.isEmpty()) {
                    double rate = Double.parseDouble(nightlyRateField.getText());
                    calculateAndUpdateTotal(rate, nightsField, totalLabel);
                } else {
                    totalLabel.setText("Total: K 0.00");
                }
            } catch (NumberFormatException e) {
                totalLabel.setText("Total: K 0.00");
            }
        });

        Button saveBillBtn = new Button("Save Bill & Occupy Room");
        saveBillBtn.setMaxWidth(Double.MAX_VALUE);
        saveBillBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");

        Label statusMsg = new Label();
        statusMsg.setTextFill(Color.valueOf("#16A34A"));

        saveBillBtn.setOnAction(e -> {
            try {
                String guestName = guestNameField.getText().trim();
                String roomType = roomTypeCombo.getValue();
                String nightsText = nightsField.getText().trim();
                String rateText = nightlyRateField.getText().trim();

                if (guestName.isEmpty() || roomType == null || nightsText.isEmpty() || rateText.isEmpty()) {
                    statusMsg.setTextFill(Color.valueOf("#EF4444"));
                    statusMsg.setText("Please fill in all fields!");
                    return;
                }

                double rate = Double.parseDouble(rateText);
                int nights = Integer.parseInt(nightsText);

                Bill newBill = new Bill(guestName, roomType, rate, nights);
                DatabaseService.saveReceipt(newBill);

                statusMsg.setTextFill(Color.valueOf("#16A34A"));
                statusMsg.setText("Bill saved successfully & Room marked OCCUPIED!");

                // Clear fields
                guestNameField.clear();
                roomTypeCombo.getSelectionModel().clearSelection();
                nightlyRateField.clear();
                nightsField.clear();
                totalLabel.setText("Total: K 0.00");

            } catch (NumberFormatException ex) {
                statusMsg.setTextFill(Color.valueOf("#EF4444"));
                statusMsg.setText("Invalid number format for nights!");
            }
        });

        formCard.getChildren().addAll(
                formTitle,
                new Label("Guest Name:"), guestNameField,
                new Label("Room Type:"), roomTypeCombo,
                new Label("Nightly Rate (K):"), nightlyRateField,
                new Label("Number of Nights:"), nightsField,
                totalLabel,
                saveBillBtn,
                statusMsg
        );

        // Search Bar Shortcut
        Button openSearchBtn = new Button("Search Receipts & Guest History");
        openSearchBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        openSearchBtn.setOnAction(e -> SearchDialog.show());

        centerContent.getChildren().addAll(formCard, openSearchBtn);
        root.setCenter(centerContent);

        return new Scene(root, 750, 650);
    }

    private static void calculateAndUpdateTotal(double rate, TextField nightsField, Label totalLabel) {
        try {
            int nights = Integer.parseInt(nightsField.getText().trim());
            double total = rate * nights;
            totalLabel.setText(String.format("Total: K %.2f", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("Total: K 0.00");
        }
    }
}