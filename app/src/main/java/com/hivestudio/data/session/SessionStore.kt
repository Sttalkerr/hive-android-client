package com.hivestudio.data.session

import android.content.Context
import android.content.SharedPreferences
import com.hivestudio.data.remote.model.AuthResponseDto

object SessionStore {
    private const val preferencesName = "hive_studio_session"
    private const val keyToken = "token"
    private const val keyProducerId = "producer_id"
    private const val keyEmail = "email"
    private const val keyStageName = "stage_name"

    @Volatile
    private var preferences: SharedPreferences? = null

    val currentToken: String?
        get() = preferences?.getString(keyToken, null)

    fun initialize(context: Context) {
        if (preferences != null) return
        preferences = context.applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun saveSession(authResponse: AuthResponseDto) {
        val prefs = requirePreferences()
        prefs.edit()
            .putString(keyToken, authResponse.token)
            .putString(keyProducerId, authResponse.id)
            .putString(keyEmail, authResponse.email)
            .putString(keyStageName, authResponse.stageName)
            .apply()
    }

    fun clear() {
        requirePreferences().edit().clear().apply()
    }

    fun hasActiveSession(): Boolean = !currentToken.isNullOrBlank()

    private fun requirePreferences(): SharedPreferences =
        preferences ?: error("SessionStore is not initialized")
}
