package com.hivestudio.ui.screens.beatdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.BeatDetailsUi
import com.hivestudio.ui.model.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BeatDetailsViewModel(
    private val repository: RemoteCatalogRepository = RemoteCatalogRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow<LoadState<BeatDetailsUi>>(LoadState.Loading)
    val state: StateFlow<LoadState<BeatDetailsUi>> = _state.asStateFlow()

    fun loadBeat(beatId: String) {
        viewModelScope.launch {
            if (_state.value !is LoadState.Success) {
                _state.value = LoadState.Loading
            }
            _state.value = runCatching {
                LoadState.Success(repository.loadBeatDetails(beatId))
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить бит"))
            }
        }
    }

    fun simulatePlay(beatId: String) = simulateAndReload(beatId) { repository.simulatePlay(beatId) }
    fun simulateLike(beatId: String) = simulateAndReload(beatId) { repository.simulateLike(beatId) }
    fun simulatePurchase(beatId: String) = simulateAndReload(beatId) { repository.simulatePurchase(beatId) }
    fun deleteBeat(beatId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                repository.deleteBeat(beatId)
                CatalogRefreshBus.notifyChanged()
            }.onSuccess {
                onDeleted()
            }.onFailure {
                _state.value = LoadState.Error(it.toUserMessage("Не удалось удалить бит"))
            }
        }
    }

    private fun simulateAndReload(beatId: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching {
                action()
                CatalogRefreshBus.notifyChanged()
                repository.loadBeatDetails(beatId)
            }.onSuccess {
                _state.value = LoadState.Success(it)
            }.onFailure {
                _state.value = LoadState.Error(it.toUserMessage("Не удалось обновить статистику"))
            }
        }
    }
}
