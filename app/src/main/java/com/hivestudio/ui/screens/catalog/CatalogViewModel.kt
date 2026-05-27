package com.hivestudio.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.preferences.SearchHistoryEntry
import com.hivestudio.data.preferences.SearchHistoryStore
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.BeatPriceFilter
import com.hivestudio.ui.model.BeatSortType
import com.hivestudio.ui.model.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: RemoteCatalogRepository = RemoteCatalogRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow<LoadState<List<BeatCardUi>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<BeatCardUi>>> = _state.asStateFlow()
    private val _availableGenres = MutableStateFlow<List<String>>(emptyList())
    val availableGenres: StateFlow<List<String>> = _availableGenres.asStateFlow()
    private val _searchHistory = MutableStateFlow(SearchHistoryStore.getEntries())
    val searchHistory: StateFlow<List<SearchHistoryEntry>> = _searchHistory.asStateFlow()
    private var currentQuery: String? = null
    private var currentSort: BeatSortType = BeatSortType.Newest
    private var currentGenre: String? = null
    private var currentPriceFilter: BeatPriceFilter = BeatPriceFilter.Any
    private var sourceBeats: List<BeatCardUi> = emptyList()
    private var searchJob: Job? = null

    init {
        loadCatalog()
        viewModelScope.launch {
            CatalogRefreshBus.flow.collect {
                loadCatalog(currentQuery)
            }
        }
    }

    fun loadCatalog(query: String? = null) {
        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                sourceBeats = repository.loadCatalogBeatCards(query)
                _availableGenres.value = sourceBeats.map { it.genre }.distinct().sorted()
                LoadState.Success(sourceBeats.applyCatalogFilters())
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить каталог"))
            }
        }
    }

    fun retryLastQuery() {
        loadCatalog(currentQuery)
    }

    fun updateSort(sortType: BeatSortType) {
        currentSort = sortType
        applyCurrentFilters()
    }

    fun updateGenreFilter(genre: String?) {
        currentGenre = genre
        applyCurrentFilters()
    }

    fun updatePriceFilter(priceFilter: BeatPriceFilter) {
        currentPriceFilter = priceFilter
        applyCurrentFilters()
    }

    fun saveHistory(beat: BeatCardUi) {
        SearchHistoryStore.saveEntry(
            SearchHistoryEntry(
                beatId = beat.id,
                title = beat.title,
                producerStageName = beat.producerStageName,
            )
        )
        _searchHistory.value = SearchHistoryStore.getEntries()
    }

    fun clearHistory() {
        SearchHistoryStore.clear()
        _searchHistory.value = emptyList()
    }

    private fun applyCurrentFilters() {
        _state.value = LoadState.Success(sourceBeats.applyCatalogFilters())
    }

    private fun List<BeatCardUi>.applyCatalogFilters(): List<BeatCardUi> =
        filter { beat ->
            (currentGenre == null || beat.genre == currentGenre) &&
                when (currentPriceFilter) {
                    BeatPriceFilter.Any -> true
                    BeatPriceFilter.Under3000 -> beat.priceRubles < 3000
                    BeatPriceFilter.Between3000And5000 -> beat.priceRubles in 3000..5000
                    BeatPriceFilter.Over5000 -> beat.priceRubles > 5000
                }
        }.let { beats ->
            when (currentSort) {
                BeatSortType.Newest -> beats
                BeatSortType.Popular -> beats.sortedByDescending { it.plays }
                BeatSortType.PriceLowToHigh -> beats.sortedBy { it.priceRubles }
                BeatSortType.PriceHighToLow -> beats.sortedByDescending { it.priceRubles }
            }
        }
}
