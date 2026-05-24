package com.hivestudio.ui.screens.metricdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.RemoteCatalogRepository
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.model.LoadState
import com.hivestudio.ui.model.MetricTrendUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MetricDetailsViewModel(
    private val repository: RemoteCatalogRepository = RemoteCatalogRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow<LoadState<MetricTrendUi>>(LoadState.Loading)
    val state: StateFlow<LoadState<MetricTrendUi>> = _state.asStateFlow()

    fun load(metricType: AnalyticsMetricType) {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                LoadState.Success(repository.loadMetricTrend(metricType))
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить график метрики"))
            }
        }
    }
}
