package com.mupratj.controller;

import com.mupratj.service.ThemeService;

public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    public boolean isDarkMode() {
        return themeService.isDarkMode();
    }

    public boolean toggleTheme() {
        return themeService.toggleTheme();
    }
}