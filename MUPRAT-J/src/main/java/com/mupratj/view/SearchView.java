package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.User;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class SearchView {
    private final BorderPane root = new BorderPane();

    public SearchView(
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

        Label title = new Label("Search guest billing history");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        TextField search = new TextField();
        search.setPromptText("Search by guest name, room type, room number, or bill number");

        FilteredList<Booking> results = new FilteredList<>(
                FXCollections.observableArrayList(
                        bookingController.getVisibleBookings()
                ),
                booking -> true
        );

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String query = newValue == null
                    ? ""
                    : newValue.trim().toLowerCase();

            results.setPredicate(booking -> query.isEmpty()
                    || booking.getGuestName().toLowerCase().contains(query)
                    || booking.getBillNumber().toLowerCase().contains(query)
                    || booking.getRoomNumber().toLowerCase().contains(query)
                    || booking.getRoomType().getDisplayName().toLowerCase().contains(query));
        });

        TableView<Booking> table = new TableView<>(results);
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

        TableColumn<Booking, Integer> nightsColumn = new TableColumn<>("Nights");
        nightsColumn.setCellValueFactory(new PropertyValueFactory<>("nights"));

        TableColumn<Booking, String> totalColumn = moneyColumn(
                "Total", Booking::getTotal);
        TableColumn<Booking, String> balanceColumn = moneyColumn(
                "Balance", Booking::getBalance);

        table.getColumns().addAll(
                billColumn,
                guestColumn,
                roomColumn,
                nightsColumn,
                totalColumn,
                balanceColumn
        );

        Label selectedBill = new Label("Select a result to see its payment details.");
        selectedBill.setWrapText(true);

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldBooking, booking) -> {
                    if (booking == null) {
                        selectedBill.setText("Select a result to see its payment details.");
                    } else {
                        selectedBill.setText(String.format(
                                "%s — %s | Total K%.2f | Paid K%.2f | Balance K%.2f",
                                booking.getBillNumber(),
                                booking.getGuestName(),
                                booking.getTotal(),
                                booking.getAmountPaid(),
                                booking.getBalance()
                        ));
                    }
                }
        );

        VBox content = new VBox(16, title, search, table, selectedBill);
        content.setPadding(new Insets(26));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(content);
    }

    private TableColumn<Booking, String> moneyColumn(
            String heading,
            java.util.function.ToDoubleFunction<Booking> value
    ) {
        TableColumn<Booking, String> column = new TableColumn<>(heading);
        column.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
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