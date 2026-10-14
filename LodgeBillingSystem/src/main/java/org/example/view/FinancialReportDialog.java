package org.example.view;

import org.example.model.DatabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FinancialReportDialog {

    public static void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("End-of-Day Financial Summary & Analytics");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F4F6F8;");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefSize(450, 300);
        reportArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px;");
        reportArea.setText(DatabaseService.getFinancialSummaryReport());

        Button closeBtn = new Button("Close Report");
        closeBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        closeBtn.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(reportArea, closeBtn);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 500, 400);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }
}