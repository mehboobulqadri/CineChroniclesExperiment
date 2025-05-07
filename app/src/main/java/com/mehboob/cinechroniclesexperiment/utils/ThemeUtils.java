package com.mehboob.cinechroniclesexperiment.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    /**
     * Check if the device is currently in dark mode
     * @param context Application context
     * @return true if in dark mode, false otherwise
     */
    public static boolean isDarkModeEnabled(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Set the app theme to dark or light mode
     * @param darkMode true for dark mode, false for light mode
     */
    public static void setDarkMode(boolean darkMode) {
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Toggle between dark and light mode
     * @param activity Current activity
     * @param preferenceManager Preference manager to save the theme setting
     */
    public static void toggleDarkMode(Activity activity, PreferenceManager preferenceManager) {
        boolean isDarkMode = isDarkModeEnabled(activity);
        preferenceManager.setBoolean("dark_mode", !isDarkMode);

        // Apply theme without restarting
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Update status bar color
        updateStatusBarColor(activity, !isDarkMode);
    }

    /**
     * Update status bar color based on theme
     * @param activity Current activity
     * @param darkMode true for dark mode, false for light mode
     */
    public static void updateStatusBarColor(Activity activity, boolean darkMode) {
        Window window = activity.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();

            if (darkMode) {
                // Dark mode: light text on dark background
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                // Light mode: dark text on light background
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

            decorView.setSystemUiVisibility(flags);
        }
    }
}
