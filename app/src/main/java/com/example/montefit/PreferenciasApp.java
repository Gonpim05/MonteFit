package com.example.montefit;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class PreferenciasApp {
    private static final String PREFS_NAME = "MonteFitPrefs";
    private static final String KEY_THEME_MODE = "theme_mode"; // 0: Oscuro, 1: Claro
    private static final String KEY_UNIDAD_LBS = "unidad_lbs"; // false: kg, true: lbs
    private static final String KEY_ULTIMO_ENTRENO_PUBLICO = "ultimo_entreno_publico"; // true: publico, false: privado

    public static void saveThemeMode(Context context, int mode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME_MODE, mode);
        editor.apply();
        applyTheme(mode);
    }

    public static int getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, 0);
    }

    public static void applyTheme(int mode) {
        if (mode == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static void saveUnidadPeso(Context context, boolean usaLibras) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_UNIDAD_LBS, usaLibras);
        editor.apply();
    }

    public static boolean usaLibras(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_UNIDAD_LBS, false);
    }

    public static double convertirPesoAUnidadDestino(double kg, boolean aLibras) {
        if (aLibras) return kg * 2.20462;
        return kg;
    }

    public static double convertirAkgDesdeUnidadActual(double pesoActual, boolean estaEnLibras) {
        if (estaEnLibras) return pesoActual / 2.20462;
        return pesoActual;
    }
    
    public static String formatPeso(double kg, Context context) {
        boolean lbs = usaLibras(context);
        double converted = convertirPesoAUnidadDestino(kg, lbs);
        return String.format(java.util.Locale.US, "%.1f %s", converted, lbs ? "lbs" : "kg");
    }

    public static void saveUltimoEstadoPublico(Context context, boolean esPublico) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_ULTIMO_ENTRENO_PUBLICO, esPublico);
        editor.apply();
    }

    public static boolean getUltimoEstadoPublico(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ULTIMO_ENTRENO_PUBLICO, true);
    }
}
