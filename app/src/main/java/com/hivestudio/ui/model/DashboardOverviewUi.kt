package com.hivestudio.ui.model

data class DashboardOverviewUi(
    val metrics: List<DashboardMetricUi>,
    val beatsCount: Int,
    val topBeat: BeatCardUi?,
    val recentBeats: List<BeatCardUi>,
)
