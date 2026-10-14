package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.DatabaseService;

import java.util.Map;

public class RoomStatusView {

    public static void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Live Lodge Room Status Board");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F4F6F8;");

        Label title = new Label("Current Room Availability & Status");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Load room statuses and build rows dynamically with check-out capability
        reloadRoomGrid(dialog, grid);

        Button closeBtn = new Button("Close Board");
        closeBtn.setStyle("-fx-background-color: #64748B; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        closeBtn.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(title, grid, closeBtn);

        Scene scene = new Scene(layout, 500, 450);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    private static void reloadRoomGrid(Stage dialog, GridPane grid) {
        grid.getChildren().clear();
        Map<String, String> rooms = DatabaseService.getRoomStatuses();

        int i = 0;
        for (Map.Entry<String, String> entry : rooms.entrySet()) {
            String roomNumber = entry.getKey();
            String status = entry.getValue();

            Label roomLabel = new Label("Room " + roomNumber);
            roomLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

            String color = status.equals("AVAILABLE") ? "#10B981" : status.equals("OCCUPIED") ? "#EF4444" : "#F59E0B";
            Label statusLabel = new Label(" " + status + " ");
            statusLabel.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 8; -fx-font-weight: bold;", color));

            grid.add(roomLabel, 0, i);
            grid.add(statusLabel, 1, i);

            // Add Check-Out button only if room is OCCUPIED
            if (status.equals("OCCUPIED")) {
                Button checkoutBtn = new Button("Check Out");
                checkoutBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                checkoutBtn.setOnAction(e -> {
                    DatabaseService.vacateRoom(roomNumber);
                    // Refresh grid instantly after check-out
                    reloadRoomGrid(dialog, grid);
                });
                grid.add(checkoutBtn, 2, i);
            }

            i++;
        }
    }
}