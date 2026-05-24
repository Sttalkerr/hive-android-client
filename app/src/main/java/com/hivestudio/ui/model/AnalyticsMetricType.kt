package com.hivestudio.ui.model

enum class AnalyticsMetricType(
    val routeKey: String,
    val title: String,
    val shortLabel: String,
) {
    Plays("plays", "Прослушивания", "Plays"),
    Likes("likes", "Лайки", "Likes"),
    Purchases("purchases", "Покупки", "Sales"),
    Revenue("revenue", "Выручка", "Revenue");

    companion object {
        fun fromRouteKey(routeKey: String): AnalyticsMetricType =
            entries.firstOrNull { it.routeKey == routeKey } ?: Plays
    }
}
