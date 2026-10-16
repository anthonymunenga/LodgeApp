package com.mupratj;

import com.mupratj.controller.BookingController;
import com.mupratj.controller.FinancialReportController;
import com.mupratj.controller.LoginController;
import com.mupratj.controller.RoomController;
import com.mupratj.controller.ThemeController;
import com.mupratj.repository.BookingRepository;
import com.mupratj.repository.DatabaseManager;
import com.mupratj.repository.RoomRepository;
import com.mupratj.repository.UserRepository;
import com.mupratj.service.AuthService;
import com.mupratj.service.BookingService;
import com.mupratj.service.FinancialReportService;
import com.mupratj.service.RoomService;
import com.mupratj.service.ThemeService;
import com.mupratj.view.BillsView;
import com.mupratj.view.BookingView;
import com.mupratj.view.DashboardView;
import com.mupratj.view.DeletedReceiptsView;
import com.mupratj.view.FinancialReportView;
import com.mupratj.view.LoginView;
import com.mupratj.view.ReceiptView;
import com.mupratj.view.RegisterView;
import com.mupratj.view.RoomStatusView;
import com.mupratj.view.SearchView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage stage;
    private LoginController loginController;
    private BookingController bookingController;
    private RoomController roomController;
    private FinancialReportController financialReportController;
    private ThemeController themeController;
    private String currentPage = "login";

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("MUPRAT-J Lodge Management");

        DatabaseManager.initialize();

        BookingRepository bookingRepository = new BookingRepository();
        RoomRepository roomRepository = new RoomRepository();
        UserRepository userRepository = new UserRepository();

        BookingService bookingService = new BookingService(
                bookingRepository,
                roomRepository
        );
        RoomService roomService = new RoomService(roomRepository);
        AuthService authService = new AuthService(userRepository);
        ThemeService themeService = new ThemeService();

        loginController = new LoginController(authService);
        bookingController = new BookingController(
                bookingService,
                loginController
        );
        roomController = new RoomController(
                roomService,
                loginController
        );
        financialReportController = new FinancialReportController(
                new FinancialReportService(bookingService),
                loginController
        );
        themeController = new ThemeController(themeService);

        showLogin();
        stage.show();
    }

    private void showLogin() {
        currentPage = "login";
        LoginView view = new LoginView(
                loginController,
                themeController,
                user -> showDashboard(),
                this::showRegister
        );
        showScene(view.getRoot(), 1000, 700);
    }

    private void showRegister() {
        currentPage = "register";
        RegisterView view = new RegisterView(
                loginController,
                themeController,
                this::showLogin
        );
        showScene(view.getRoot(), 850, 760);
    }

    private void showDashboard() {
        showPage("dashboard");
    }

    private void showPage(String page) {
        if (loginController.getCurrentUser() == null) {
            showLogin();
            return;
        }

        currentPage = page;
        Parent content;

        switch (page) {
            case "booking" -> content = new BookingView(
                    bookingController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            case "bills" -> content = new BillsView(
                    bookingController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            case "receipts" -> content = new ReceiptView(
                    bookingController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            case "rooms" -> content = new RoomStatusView(
                    roomController,
                    bookingController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            case "financials" -> content = new FinancialReportView(
                    financialReportController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            case "archive" -> content = new DeletedReceiptsView(
                    bookingController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            case "search" -> content = new SearchView(
                    bookingController,
                    themeController,
                    loginController.getCurrentUser(),
                    this::navigate,
                    this::logout
            ).getRoot();

            default -> {
                currentPage = "dashboard";
                content = new DashboardView(
                        bookingController,
                        themeController,
                        loginController.getCurrentUser(),
                        this::navigate,
                        this::logout
                ).getRoot();
            }
        }

        showScene(content, 1280, 820);
    }

    private void navigate(String page) {
        if ("theme".equals(page)) {
            showPage(currentPage);
        } else {
            showPage(page);
        }
    }

    private void logout() {
        loginController.logout();
        showLogin();
    }

    private void showScene(Parent root, double width, double height) {
        stage.setScene(new Scene(root, width, height));
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}