package com.hivestudio.ui.screens.beats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.BeatSortType
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
    private var currentSort: BeatSortType = BeatSortType.Newest

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
                val beats = repository.loadBeatCards(query).sortedBy(currentSort)
                LoadState.Success(beats)
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить список битов"))
            }
        }
    }

    fun updateSort(sortType: BeatSortType) {
        currentSort = sortType
        loadBeats(currentQuery)
    }

}

private fun List<BeatCardUi>.sortedBy(sortType: BeatSortType): List<BeatCardUi> =
    when (sortType) {
        BeatSortType.Newest -> this
        BeatSortType.Popular -> sortedByDescending { it.plays }
        BeatSortType.PriceLowToHigh -> sortedBy { it.priceRubles }
        BeatSortType.PriceHighToLow -> sortedByDescending { it.priceRubles }
    }
