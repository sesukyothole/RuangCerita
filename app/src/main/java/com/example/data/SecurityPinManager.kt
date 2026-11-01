package com.example.data

import android.content.Context

object SecurityPinManager {
    private const val PREFS_NAME = "app_security_pin_prefs"
    private const val KEY_PIN = "dashboard_age_gate_pin"

    const val DEFAULT_PIN = "1234"
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_PASSWORD = "admin1234"

    fun getPin(context: Context): String {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sp.getString(KEY_PIN, DEFAULT_PIN) ?: DEFAULT_PIN
    }

    fun setPin(context: Context, newPin: String): Boolean {
        val cleanPin = newPin.trim()
        if (cleanPin.length == 4 && cleanPin.all { it.isDigit() }) {
            val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putString(KEY_PIN, cleanPin).apply()
            return true
        }
        return false
    }

    fun verifyPin(context: Context, inputPin: String): Boolean {
        return getPin(context) == inputPin.trim()
    }

    fun verifyAdminCredentials(user: String, pass: String): Boolean {
        return user.trim() == ADMIN_USERNAME && pass.trim() == ADMIN_PASSWORD
    }
}
