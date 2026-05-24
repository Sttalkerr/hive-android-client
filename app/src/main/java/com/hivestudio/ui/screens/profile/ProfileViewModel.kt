package com.hivestudio.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.ApiConfig
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.AuthRepository
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.ui.model.LoadState
import com.hivestudio.ui.model.ProfileUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _profileState = MutableStateFlow<LoadState<ProfileUi>>(LoadState.Loading)
    val profileState: StateFlow<LoadState<ProfileUi>> = _profileState.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _hasSession = MutableStateFlow(authRepository.hasActiveSession())
    val hasSession: StateFlow<Boolean> = _hasSession.asStateFlow()

    init {
        if (authRepository.hasActiveSession()) {
            refreshProfile()
        } else {
            _profileState.value = LoadState.Error("Выполни вход, чтобы открыть профиль продюсера")
        }
        viewModelScope.launch {
            CatalogRefreshBus.flow.collect {
                if (authRepository.hasActiveSession()) {
                    refreshProfile()
                } else {
                    _hasSession.value = false
                    _profileState.value = LoadState.Error("Выполни вход, чтобы открыть профиль продюсера")
                }
            }
        }
    }

    fun refreshProfile() {
        if (!authRepository.hasActiveSession()) {
            _hasSession.value = false
            _profileState.value = LoadState.Error("Выполни вход, чтобы открыть профиль продюсера")
            return
        }

        viewModelScope.launch {
            _hasSession.value = true
            _profileState.value = LoadState.Loading
            _profileState.value = runCatching {
                LoadState.Success(authRepository.loadProfile().toProfileUi())
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить профиль"))
            }
        }
    }

    fun saveProfile(
        stageName: String,
        bio: String,
        city: String,
        contactTag: String,
    ) {
        viewModelScope.launch {
            _message.value = ""
            runCatching {
                authRepository.updateProfile(stageName, bio, city, contactTag).toProfileUi()
            }.onSuccess {
                _profileState.value = LoadState.Success(it)
                _message.value = "Профиль обновлен"
                CatalogRefreshBus.notifyChanged()
            }.onFailure {
                _message.value = it.toUserMessage("Не удалось сохранить профиль")
            }
        }
    }

    fun uploadAvatar(
        context: Context,
        avatarUri: Uri,
    ) {
        viewModelScope.launch {
            _message.value = ""
            runCatching {
                authRepository.uploadAvatar(context, avatarUri).toProfileUi()
            }.onSuccess {
                _profileState.value = LoadState.Success(it)
                _message.value = "Аватар обновлен"
                CatalogRefreshBus.notifyChanged()
            }.onFailure {
                _message.value = it.toUserMessage("Не удалось загрузить аватар")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _hasSession.value = false
        _message.value = "Сессия завершена"
        _profileState.value = LoadState.Error("Выполни вход, чтобы открыть профиль продюсера")
        CatalogRefreshBus.notifyChanged()
    }
}

private fun com.hivestudio.data.remote.model.ProfileDto.toProfileUi(): ProfileUi =
    ProfileUi(
        id = id,
        email = email,
        stageName = stageName,
        bio = bio,
        city = city,
        contactTag = contactTag,
        avatarUrl = avatarUrl?.toAbsoluteApiUrl(),
        beatsCount = beatsCount,
        totalPlays = totalPlays,
        totalRevenue = totalRevenue.roundToInt(),
        createdAt = createdAt,
    )

private fun String.toAbsoluteApiUrl(): String =
    if (startsWith("http://") || startsWith("https://")) this else "${ApiConfig.BASE_URL}${removePrefix("/")}"
