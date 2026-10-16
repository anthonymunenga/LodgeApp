package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class DeletedReceiptsView {
    private final BorderPane root = new BorderPane();

    public DeletedReceiptsView(
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

        Label title = new Label("Deleted receipt archive");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        Label description = new Label(
                "Archived receipts are retained for administrator review and can be restored."
        );
        description.setWrapText(true);

        TableView<Booking> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, String> billColumn = new TableColumn<>("Bill");
        billColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));

        TableColumn<Booking, String> guestColumn = new TableColumn<>("Guest");
        guestColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));

        TableColumn<Booking, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getRoomType().getDisplayName()
                                + " / " + cell.getValue().getRoomNumber()
                )
        );

        TableColumn<Booking, String> deletedByColumn = new TableColumn<>("Deleted by");
        deletedByColumn.setCellValueFactory(new PropertyValueFactory<>("deletedBy"));

        TableColumn<Booking, String> deletedAtColumn = new TableColumn<>("Deleted at");
        deletedAtColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDeletedAt() == null
                                ? "Unknown"
                                : cell.getValue().getDeletedAt().toString()
                )
        );

        table.getColumns().addAll(
                billColumn,
                guestColumn,
                roomColumn,
                deletedByColumn,
                deletedAtColumn
        );

        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        List<Booking> deletedBookings = isAdmin
                ? bookingController.getDeletedBookings()
                : List.of();
        table.setItems(FXCollections.observableArrayList(deletedBookings));

        Label message = new Label();
        Button restore = new Button("Restore selected receipt");
        restore.setDisable(!isAdmin);
        restore.setOnAction(event -> {
            Booking selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a receipt first.");
            } else if (bookingController.restoreReceipt(selected)) {
                table.getItems().remove(selected);
                message.setText("Receipt restored to active bills.");
            } else {
                message.setText("Only an administrator can restore receipts.");
            }
        });

        if (!isAdmin) {
            message.setText("Administrator access is required to view this archive.");
        }

        VBox content = new VBox(
                14,
                title,
                description,
                table,
                new HBox(12, restore, message)
        );
        content.setPadding(new Insets(26));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(content);
    }

    public Parent getRoot() {
        return root;
    }
}