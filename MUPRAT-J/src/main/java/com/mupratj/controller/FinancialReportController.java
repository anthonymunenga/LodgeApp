package com.mupratj.controller;

import com.mupratj.model.FinancialPeriod;
import com.mupratj.model.FinancialSummary;
import com.mupratj.service.FinancialReportService;

public class FinancialReportController {
    private final FinancialReportService reportService;
    private final LoginController loginController;

    public FinancialReportController(
            FinancialReportService reportService,
            LoginController loginController
    ) {
        this.reportService = reportService;
        this.loginController = loginController;
    }

    public FinancialSummary getReport(FinancialPeriod period) {
        return reportService.createReport(
                loginController.getCurrentUser(),
                period
        );
    }
}