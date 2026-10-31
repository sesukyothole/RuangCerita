package com.example.data

import android.content.Context

enum class AppThemeMode(val title: String, val subtitle: String) {
    SYSTEM("Default Perangkat", "Mengikuti setelan tema sistem HP"),
    LIGHT("Warna Terang", "Tampilan cerah, elegan dan bersih"),
    DARK("Warna Gelap", "Tampilan mewah malam dan hemat baterai");

    companion object {
        private const val PREFS_NAME = "app_theme_preferences"
        private const val KEY_THEME_MODE = "key_theme_mode"

        fun loadFromPreferences(context: Context): AppThemeMode {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val saved = prefs.getString(KEY_THEME_MODE, SYSTEM.name) ?: SYSTEM.name
            return try {
                valueOf(saved)
            } catch (_: Exception) {
                SYSTEM
            }
        }

        fun saveToPreferences(context: Context, mode: AppThemeMode) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        }
    }
}
