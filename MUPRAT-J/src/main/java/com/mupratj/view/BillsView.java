package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class BillsView {
    private final BorderPane root = new BorderPane();

    public BillsView(
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

        Label title = new Label("Bills and guest balances");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        TextField search = new TextField();
        search.setPromptText("Search by guest, bill number, or room");

        List<Booking> records = bookingController.getVisibleBookings();
        FilteredList<Booking> filtered = new FilteredList<>(
                FXCollections.observableArrayList(records),
                booking -> true
        );

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String query = newValue == null
                    ? ""
                    : newValue.trim().toLowerCase();

            filtered.setPredicate(booking -> query.isEmpty()
                    || booking.getGuestName().toLowerCase().contains(query)
                    || booking.getBillNumber().toLowerCase().contains(query)
                    || booking.getRoomNumber().toLowerCase().contains(query)
                    || booking.getRoomType().getDisplayName()
                    .toLowerCase().contains(query));
        });

        TableView<Booking> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, String> billColumn = new TableColumn<>("Bill");
        billColumn.setCellValueFactory(
                new PropertyValueFactory<>("billNumber")
        );

        TableColumn<Booking, String> guestColumn = new TableColumn<>("Guest");
        guestColumn.setCellValueFactory(
                new PropertyValueFactory<>("guestName")
        );

        TableColumn<Booking, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getRoomType().getDisplayName()
                                + " / " + cell.getValue().getRoomNumber()
                )
        );

        TableColumn<Booking, Integer> nightsColumn =
                new TableColumn<>("Nights");
        nightsColumn.setCellValueFactory(
                new PropertyValueFactory<>("nights")
        );

        TableColumn<Booking, String> totalColumn =
                moneyColumn("Total", Booking::getTotal);
        TableColumn<Booking, String> paidColumn =
                moneyColumn("Paid", Booking::getAmountPaid);
        TableColumn<Booking, String> balanceColumn =
                moneyColumn("Balance", Booking::getBalance);

        table.getColumns().addAll(
                billColumn,
                guestColumn,
                roomColumn,
                nightsColumn,
                totalColumn,
                paidColumn,
                balanceColumn
        );
        table.setItems(filtered);

        Label details = new Label("Select a bill to review it.");
        details.setWrapText(true);

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldBooking, selected) -> {
                    if (selected == null) {
                        details.setText("Select a bill to review it.");
                        return;
                    }

                    details.setText(String.format(
                            "%s — %s | Room %s | Total K%.2f | Paid K%.2f | Balance K%.2f",
                            selected.getBillNumber(),
                            selected.getGuestName(),
                            selected.getRoomNumber(),
                            selected.getTotal(),
                            selected.getAmountPaid(),
                            selected.getBalance()
                    ));
                }
        );

        Label message = new Label();
        Button archiveButton =
                new Button("Delete selected receipt (move to archive)");

        archiveButton.setOnAction(event -> {
            Booking selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a receipt first.");
                return;
            }

            if (bookingController.deleteReceipt(selected)) {
                filtered.getSource().remove(selected);
                table.getSelectionModel().clearSelection();
                details.setText("Select a bill to review it.");
                message.setText(
                        "Receipt archived. An administrator can view it in Deleted Archive."
                );
            } else {
                message.setText(
                        "The receipt could not be archived. Check your account permissions."
                );
            }
        });

        VBox content = new VBox(
                16,
                title,
                search,
                table,
                details,
                new HBox(12, archiveButton, message)
        );
        content.setPadding(new Insets(26));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);
    }

    private TableColumn<Booking, String> moneyColumn(
            String heading,
            ToDoubleFunction<Booking> value
    ) {
        TableColumn<Booking, String> column = new TableColumn<>(heading);
        column.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format(
                                "K%.2f",
                                value.applyAsDouble(cell.getValue())
                        )
                )
        );
        return column;
    }

    public Parent getRoot() {
        return root;
    }
}