package com.mupratj.view;

import com.mupratj.controller.FinancialReportController;
import com.mupratj.controller.ThemeController;
import com.mupratj.model.Booking;
import com.mupratj.model.FinancialPeriod;
import com.mupratj.model.FinancialSummary;
import com.mupratj.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class FinancialReportView {
    private final BorderPane root = new BorderPane();

    public FinancialReportView(
            FinancialReportController reportController,
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

        Label title = new Label("Financial records");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        ComboBox<FinancialPeriod> periodPicker = new ComboBox<>();
        periodPicker.getItems().setAll(FinancialPeriod.values());
        periodPicker.setValue(FinancialPeriod.TODAY);

        Label billCount = new Label();
        Label revenue = new Label();
        Label paid = new Label();
        Label outstanding = new Label();

        HBox totals = new HBox(
                20,
                billCount,
                revenue,
                paid,
                outstanding
        );

        TableView<Booking> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, String> billColumn = new TableColumn<>("Bill");
        billColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));

        TableColumn<Booking, String> guestColumn = new TableColumn<>("Customer");
        guestColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));

        TableColumn<Booking, String> totalColumn = moneyColumn(
                "Total", Booking::getTotal);
        TableColumn<Booking, String> paidColumn = moneyColumn(
                "Paid", Booking::getAmountPaid);
        TableColumn<Booking, String> balanceColumn = moneyColumn(
                "Balance", Booking::getBalance);

        table.getColumns().addAll(
                billColumn,
                guestColumn,
                totalColumn,
                paidColumn,
                balanceColumn
        );

        TextArea customerBalances = new TextArea();
        customerBalances.setEditable(false);
        customerBalances.setWrapText(true);
        customerBalances.setPrefRowCount(6);

        Runnable refreshReport = () -> {
            FinancialSummary summary = reportController.getReport(
                    periodPicker.getValue()
            );

            billCount.setText("Bills: " + summary.getBillCount());
            revenue.setText(String.format(
                    "Total billed: K%.2f", summary.getTotalRevenue()));
            paid.setText(String.format(
                    "Paid: K%.2f", summary.getTotalPaid()));
            outstanding.setText(String.format(
                    "Outstanding: K%.2f", summary.getTotalBalance()));

            table.setItems(FXCollections.observableArrayList(
                    summary.getBookings()
            ));

            StringBuilder balances = new StringBuilder();
            if (summary.getCustomerBalances().isEmpty()) {
                balances.append("No bills found for this period.");
            } else {
                summary.getCustomerBalances().forEach((customer, amount) ->
                        balances.append(customer)
                                .append(" — K")
                                .append(String.format("%.2f", amount))
                                .append(System.lineSeparator())
                );
            }

            customerBalances.setText(balances.toString());
        };

        periodPicker.setOnAction(event -> refreshReport.run());
        refreshReport.run();

        VBox content = new VBox(
                14,
                title,
                new HBox(10, new Label("Period:"), periodPicker),
                totals,
                table,
                new Label("Outstanding balance by customer"),
                customerBalances
        );
        content.setPadding(new Insets(26));
        VBox.setVgrow(table, Priority.ALWAYS);
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