package com.hivestudio.ui.screens.beats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BeatsViewModel(
    private val repository: RemoteCatalogRepository = RemoteCatalogRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow<LoadState<List<BeatCardUi>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<BeatCardUi>>> = _state.asStateFlow()

    fun loadBeats(query: String?) {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                LoadState.Success(repository.loadBeatCards(query))
            }.getOrElse {
                LoadState.Error(it.message ?: "Не удалось загрузить список битов")
            }
        }
    }
}
