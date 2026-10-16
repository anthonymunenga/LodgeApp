package com.mupratj.service;

import java.util.prefs.Preferences;

public class ThemeService {
    private static final String DARK_MODE_KEY = "darkMode";

    private final Preferences preferences =
            Preferences.userNodeForPackage(ThemeService.class);

    public boolean isDarkMode() {
        return preferences.getBoolean(DARK_MODE_KEY, false);
    }

    public void setDarkMode(boolean darkMode) {
        preferences.putBoolean(DARK_MODE_KEY, darkMode);
    }

    public boolean toggleTheme() {
        boolean newValue = !isDarkMode();
        setDarkMode(newValue);
        return newValue;
    }
}