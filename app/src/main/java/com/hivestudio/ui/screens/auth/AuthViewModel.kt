package com.hivestudio.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.AuthRepository
import com.hivestudio.data.repository.CatalogRefreshBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = ""
            runCatching {
                authRepository.login(email, password)
            }.onSuccess {
                CatalogRefreshBus.notifyChanged()
                onSuccess()
            }.onFailure {
                _message.value = it.toUserMessage("Не удалось войти")
            }
            _isLoading.value = false
        }
    }

    fun register(
        email: String,
        password: String,
        stageName: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = ""
            runCatching {
                authRepository.register(email, password, stageName)
            }.onSuccess {
                CatalogRefreshBus.notifyChanged()
                onSuccess()
            }.onFailure {
                _message.value = it.toUserMessage("Не удалось зарегистрироваться")
            }
            _isLoading.value = false
        }
    }
}
