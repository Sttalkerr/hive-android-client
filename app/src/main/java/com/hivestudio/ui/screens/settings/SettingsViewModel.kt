package com.hivestudio.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.repository.AuthRepository
import com.hivestudio.ui.model.LoadState
import com.hivestudio.ui.model.ProfileUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _profileState = MutableStateFlow<LoadState<ProfileUi>>(LoadState.Loading)
    val profileState: StateFlow<LoadState<ProfileUi>> = _profileState.asStateFlow()

    private val _messageState = MutableStateFlow("")
    val messageState: StateFlow<String> = _messageState.asStateFlow()

    init {
        refreshProfile()
    }

    fun register(email: String, password: String, stageName: String) {
        viewModelScope.launch {
            _messageState.value = ""
            runCatching {
                authRepository.register(email, password, stageName)
            }.onSuccess {
                _messageState.value = "Регистрация выполнена"
                refreshProfile()
            }.onFailure {
                _messageState.value = it.message ?: "Не удалось зарегистрироваться"
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _messageState.value = ""
            runCatching {
                authRepository.login(email, password)
            }.onSuccess {
                _messageState.value = "Вход выполнен"
                refreshProfile()
            }.onFailure {
                _messageState.value = it.message ?: "Не удалось войти"
            }
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            _profileState.value = LoadState.Loading
            _profileState.value = runCatching {
                val profile = authRepository.loadProfile()
                LoadState.Success(
                    ProfileUi(
                        email = profile.email,
                        stageName = profile.stageName,
                        createdAt = profile.createdAt,
                    )
                )
            }.getOrElse {
                LoadState.Error(it.message ?: "Не удалось загрузить профиль")
            }
        }
    }
}
