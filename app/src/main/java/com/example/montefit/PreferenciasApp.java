package com.example.montefit;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class PreferenciasApp {
    private static final String PREFS_NAME = "MonteFitPrefs";
    private static final String KEY_THEME_MODE = "theme_mode"; // 0: Dark, 1: Light

    public static void saveThemeMode(Context context, int mode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME_MODE, mode);
        editor.apply();
        applyTheme(mode);
    }

    public static int getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, 0); // Default to Dark
    }

    public static void applyTheme(int mode) {
        if (mode == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
