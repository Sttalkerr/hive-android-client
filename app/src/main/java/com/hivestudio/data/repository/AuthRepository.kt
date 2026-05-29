package com.hivestudio.data.repository

import android.content.Context
import android.net.Uri
import com.hivestudio.data.remote.HiveStudioApi
import com.hivestudio.data.remote.HiveStudioApiFactory
import com.hivestudio.data.remote.model.AuthRequestDto
import com.hivestudio.data.remote.model.AuthResponseDto
import com.hivestudio.data.remote.model.ProfileDto
import com.hivestudio.data.remote.model.UpdateProfileRequestDto
import com.hivestudio.data.session.SessionStore

class AuthRepository(
    private val api: HiveStudioApi = HiveStudioApiFactory.create(),
) {
    suspend fun register(
        email: String,
        password: String,
        stageName: String,
    ): AuthResponseDto =
        api.register(
            AuthRequestDto(
                email = email.trim(),
                password = password,
                stageName = stageName.trim(),
            )
        ).also {
            SessionStore.saveSession(it)
            ClientDataCache.clearAll()
        }

    suspend fun login(
        email: String,
        password: String,
    ): AuthResponseDto =
        api.login(
            AuthRequestDto(
                email = email.trim(),
                password = password,
            )
        ).also {
            SessionStore.saveSession(it)
            ClientDataCache.clearAll()
        }

    suspend fun loadProfile(forceRefresh: Boolean = false): ProfileDto {
        if (!forceRefresh) {
            ClientDataCache.profile?.let { return it }
        }
        return api.getProfile().also { ClientDataCache.profile = it }
    }

    suspend fun updateProfile(
        stageName: String,
        bio: String,
        city: String,
        contactTag: String,
    ): ProfileDto =
        api.updateProfile(
            UpdateProfileRequestDto(
                stageName = stageName.trim(),
                bio = bio.trim(),
                city = city.trim(),
                contactTag = contactTag.trim(),
            )
        ).also { ClientDataCache.profile = it }

    suspend fun uploadAvatar(
        context: Context,
        avatarUri: Uri,
    ): ProfileDto =
        api.uploadAvatar(
            avatar = context.contentResolver.toMultipartPart(
                uri = avatarUri,
                formField = "avatar",
                fallbackName = "avatar.jpg",
                mediaType = "image/*",
            )
        ).also { ClientDataCache.profile = it }

    fun hasActiveSession(): Boolean = SessionStore.hasActiveSession()

    fun logout() {
        SessionStore.clear()
        ClientDataCache.clearAll()
    }
}
