package org.example.view;

import org.example.model.DatabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SearchDialog {

    public static void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Search Visitor Receipts");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F4F6F8;");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter guest name to search...");

        Button searchBtn = new Button("Search Records");
        searchBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");

        ListView<String> resultsListView = new ListView<>();
        resultsListView.setPrefHeight(250);

        searchBtn.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            resultsListView.getItems().clear();
            resultsListView.getItems().addAll(DatabaseService.searchReceipts(keyword));
        });

        // Load all initially
        resultsListView.getItems().addAll(DatabaseService.searchReceipts(""));

        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(new Label("Search Historical Database:"), searchBox, resultsListView);

        Scene scene = new Scene(layout, 500, 400);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }
}