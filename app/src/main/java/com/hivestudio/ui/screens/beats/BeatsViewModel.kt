package com.hivestudio.ui.screens.beats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.repository.CatalogRefreshBus
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
    private var currentQuery: String? = null

    init {
        viewModelScope.launch {
            CatalogRefreshBus.flow.collect {
                loadBeats(currentQuery)
            }
        }
    }

    fun loadBeats(query: String?) {
        currentQuery = query
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                LoadState.Success(repository.loadBeatCards(query))
            }.getOrElse {
                LoadState.Error(it.message ?: "Не удалось загрузить список битов")
            }
        }
    }

    fun deleteBeat(beatId: String) {
        viewModelScope.launch {
            runCatching {
                repository.deleteBeat(beatId)
                CatalogRefreshBus.notifyChanged()
            }.onFailure {
                _state.value = LoadState.Error(it.message ?: "Не удалось удалить бит")
            }
        }
    }
}
