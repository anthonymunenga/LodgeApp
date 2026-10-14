package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.model.DatabaseService;
import org.example.view.DashboardView;
import org.example.view.LoginView;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize SQLite database tables and inventory
        DatabaseService.initializeDatabase();
        DatabaseService.initializeRoomsTable();

        primaryStage.setTitle("Lodge Billing System");
        showLogin();
    }

    public void showLogin() {
        primaryStage.setScene(LoginView.createScene(this));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void showDashboard() {
        primaryStage.setScene(DashboardView.createScene(this));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}