package com.mupratj.view;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.RoomController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.Room;
import com.mupratj.model.RoomType;
import com.mupratj.model.User;
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
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class RoomStatusView {
    private final BorderPane root = new BorderPane();

    public RoomStatusView(
            RoomController roomController,
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

        Label title = new Label("Room status");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        Label inventory = new Label(
                "100 rooms: 30 Standard • 50 Family • 20 Premium"
        );

        TextField search = new TextField();
        search.setPromptText("Filter by room number or room type");

        List<Room> allRooms = roomController.getAllRooms();
        FilteredList<Room> filtered = new FilteredList<>(
                FXCollections.observableArrayList(allRooms),
                room -> true
        );

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String query = newValue == null
                    ? ""
                    : newValue.trim().toLowerCase();

            filtered.setPredicate(room -> query.isEmpty()
                    || room.getRoomNumber().toLowerCase().contains(query)
                    || room.getRoomType().getDisplayName().toLowerCase().contains(query));
        });

        TableView<Room> table = new TableView<>(filtered);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getRoomType().getDisplayName()
                )
        );

        TableColumn<Room, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getStatus().name()
                )
        );

        TableColumn<Room, String> guestColumn = new TableColumn<>("Guest");
        guestColumn.setCellValueFactory(cell -> {
            Room room = cell.getValue();
            Booking booking = findBooking(
                    bookingController.getVisibleBookings(),
                    room.getRoomNumber()
            );

            if (booking == null) {
                return new javafx.beans.property.SimpleStringProperty(
                        room.getStatus().name().equals("OCCUPIED") ? "Reserved" : "—"
                );
            }

            return new javafx.beans.property.SimpleStringProperty(
                    currentUser.isAdmin() ? booking.getGuestName() : "Your guest"
            );
        });

        TableColumn<Room, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final Button checkOut = new Button("Check Out");

            {
                checkOut.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    Booking booking = findBooking(
                            bookingController.getVisibleBookings(),
                            room.getRoomNumber()
                    );

                    if (booking != null && bookingController.checkOut(booking)) {
                        getTableView().getItems().setAll(roomController.getAllRooms());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                Room room = getTableView().getItems().get(getIndex());
                Booking booking = findBooking(
                        bookingController.getVisibleBookings(),
                        room.getRoomNumber()
                );

                setGraphic(
                        room.getStatus().name().equals("OCCUPIED") && booking != null
                                ? checkOut
                                : null
                );
            }
        });

        table.getColumns().addAll(
                roomColumn,
                typeColumn,
                statusColumn,
                guestColumn,
                actionColumn
        );

        VBox content = new VBox(14, title, inventory, search, table);
        content.setPadding(new Insets(26));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(content);
    }

    private Booking findBooking(List<Booking> bookings, String roomNumber) {
        return bookings.stream()
                .filter(booking -> !booking.isCheckedOut())
                .filter(booking -> booking.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElse(null);
    }

    public Parent getRoot() {
        return root;
    }
}