package com.hivestudio.data.preferences

import android.content.Context
import android.content.SharedPreferences

object ThemePreferenceStore {
    private const val preferencesName = "hive_studio_theme"
    private const val keyDarkThemeEnabled = "dark_theme_enabled"

    @Volatile
    private var preferences: SharedPreferences? = null

    fun initialize(context: Context) {
        if (preferences != null) return
        preferences = context.applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun isDarkThemeEnabled(): Boolean =
        requirePreferences().getBoolean(keyDarkThemeEnabled, true)

    fun setDarkThemeEnabled(enabled: Boolean) {
        requirePreferences().edit().putBoolean(keyDarkThemeEnabled, enabled).apply()
    }

    private fun requirePreferences(): SharedPreferences =
        preferences ?: error("ThemePreferenceStore is not initialized")
}
