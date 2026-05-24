package com.hivestudio.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.DashboardMetricUi
import com.hivestudio.ui.model.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: RemoteCatalogRepository = RemoteCatalogRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow<LoadState<List<DashboardMetricUi>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<DashboardMetricUi>>> = _state.asStateFlow()

    init {
        loadDashboard()
        viewModelScope.launch {
            CatalogRefreshBus.flow.collect {
                loadDashboard()
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                LoadState.Success(repository.loadDashboardMetrics())
            }.getOrElse {
                LoadState.Error(it.message ?: "Не удалось загрузить аналитику")
            }
        }
    }
}
