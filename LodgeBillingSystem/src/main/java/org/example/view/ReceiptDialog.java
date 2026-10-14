package org.example.view;

import org.example.model.Bill;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReceiptDialog {

    public static void show(Bill bill) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Lodge Billing Receipt - Saved to Database");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle("-fx-background-color: white;");

        Label header = new Label("--- OFFICIAL LODGE RECEIPT ---");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;");

        Label details = new Label(
                "Guest Name: " + bill.getGuestName() + "\n" +
                        "Room Type: " + bill.getRoomType() + "\n" +
                        "Stay Duration: " + bill.getNights() + " night(s)\n" +
                        "Rate / Night: K " + String.format("%.2f", bill.getNightlyRate()) + "\n\n" +
                        "TOTAL DUE: K " + String.format("%.2f", bill.getTotalAmount()) + "\n\n" +
                        "✓ Status: Saved securely to Database"
        );
        details.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155;");

        Button closeBtn = new Button("Close Receipt");
        closeBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        closeBtn.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(header, details, closeBtn);

        Scene scene = new Scene(layout, 380, 350);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }
}