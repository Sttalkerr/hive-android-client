package com.hivestudio.ui.screens.editbeat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditBeatViewModel(
    private val repository: RemoteCatalogRepository = RemoteCatalogRepository(),
) : ViewModel() {
    private val _beatState = MutableStateFlow<LoadState<BeatCardUi>>(LoadState.Loading)
    val beatState: StateFlow<LoadState<BeatCardUi>> = _beatState.asStateFlow()

    private val _saveState = MutableStateFlow<LoadState<String>>(LoadState.Success(""))
    val saveState: StateFlow<LoadState<String>> = _saveState.asStateFlow()

    fun loadBeat(beatId: String) {
        viewModelScope.launch {
            _beatState.value = LoadState.Loading
            _beatState.value = runCatching {
                LoadState.Success(repository.loadBeatDetails(beatId).beat)
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить бит для редактирования"))
            }
        }
    }

    fun updateBeat(
        beatId: String,
        title: String,
        genre: String,
        bpm: String,
        priceRubles: String,
        description: String,
        onSaved: () -> Unit,
    ) {
        val bpmValue = bpm.toIntOrNull()
        val priceValue = priceRubles.toIntOrNull()
        if (title.isBlank() || genre.isBlank() || description.isBlank() || bpmValue == null || priceValue == null) {
            _saveState.value = LoadState.Error("Заполни название, жанр, BPM, цену и описание")
            return
        }

        viewModelScope.launch {
            _saveState.value = LoadState.Loading
            runCatching {
                repository.updateBeat(
                    beatId = beatId,
                    title = title,
                    genre = genre,
                    bpm = bpmValue,
                    priceRubles = priceValue,
                    description = description,
                )
            }.onSuccess {
                CatalogRefreshBus.notifyChanged()
                _saveState.value = LoadState.Success("Бит обновлен")
                onSaved()
            }.onFailure {
                _saveState.value = LoadState.Error(it.toUserMessage("Не удалось обновить бит"))
            }
        }
    }
}
