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

    fun getEntries(): List<SearchHistoryEntry> {
        val raw = requirePreferences().getString(keyEntries, null).orEmpty()
        if (raw.isBlank()) return emptyList()
        val type = object : TypeToken<List<SearchHistoryEntry>>() {}.type
        return runCatching { gson.fromJson<List<SearchHistoryEntry>>(raw, type) }.getOrElse { emptyList() }
    }

    fun saveEntry(entry: SearchHistoryEntry) {
        val updated = buildList {
            add(entry)
            addAll(getEntries().filterNot { it.beatId == entry.beatId })
        }.take(maxEntries)
        requirePreferences().edit().putString(keyEntries, gson.toJson(updated)).apply()
    }

    fun clear() {
        requirePreferences().edit().remove(keyEntries).apply()
    }

    private fun requirePreferences(): SharedPreferences =
        preferences ?: error("SearchHistoryStore is not initialized")
}

data class SearchHistoryEntry(
    val beatId: String,
    val title: String,
    val producerStageName: String,
)
