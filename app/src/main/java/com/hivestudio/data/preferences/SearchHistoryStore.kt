package com.hivestudio.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SearchHistoryStore {
    private const val preferencesName = "hive_studio_search_history"
    private const val keyEntries = "entries"
    private const val maxEntries = 10

    @Volatile
    private var preferences: SharedPreferences? = null
    private val gson = Gson()

    fun initialize(context: Context) {
        if (preferences != null) return
        preferences = context.applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun getEntries(): List<String> {
        val raw = requirePreferences().getString(keyEntries, null).orEmpty()
        if (raw.isBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return runCatching { gson.fromJson<List<String>>(raw, type) }.getOrElse { emptyList() }
    }

    fun saveEntry(entry: String) {
        val normalized = entry.trim()
        if (normalized.isBlank()) return
        val updated = buildList {
            add(normalized)
            addAll(getEntries().filterNot { it.equals(normalized, ignoreCase = true) })
        }.take(maxEntries)
        requirePreferences().edit().putString(keyEntries, gson.toJson(updated)).apply()
    }

    fun clear() {
        requirePreferences().edit().remove(keyEntries).apply()
    }

    private fun requirePreferences(): SharedPreferences =
        preferences ?: error("SearchHistoryStore is not initialized")
}
