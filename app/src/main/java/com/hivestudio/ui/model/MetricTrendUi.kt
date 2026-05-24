package com.hivestudio.ui.model

data class MetricTrendPointUi(
    val label: String,
    val value: Float,
    val formattedValue: String,
)

data class MetricTrendUi(
    val type: AnalyticsMetricType,
    val title: String,
    val totalValue: String,
    val points: List<MetricTrendPointUi>,
)
