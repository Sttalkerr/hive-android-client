package com.hivestudio.ui.preview

import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.model.DashboardMetricUi

object HiveStudioPreviewData {
    val dashboardMetrics = listOf(
        DashboardMetricUi(AnalyticsMetricType.Plays, "Прослушивания", "200"),
        DashboardMetricUi(AnalyticsMetricType.Likes, "Лайки", "58"),
        DashboardMetricUi(AnalyticsMetricType.Purchases, "Покупки", "13"),
        DashboardMetricUi(AnalyticsMetricType.Revenue, "Выручка", "36 870 ₽"),
    )

    val beats = listOf(
        BeatCardUi(
            id = "preview-1",
            producerId = "producer-preview",
            producerStageName = "Hive Demo",
            title = "Midnight Pulse",
            genre = "Trap",
            bpm = 140,
            priceRubles = 2990,
            coverImageFileName = "midnight-pulse-cover.jpg",
            audioPreviewUrl = null,
            coverImageUrl = null,
            description = "Темный ударный бит для ночного вайба и плотного речитатива.",
            plays = 124,
        ),
        BeatCardUi(
            id = "preview-2",
            producerId = "producer-preview",
            producerStageName = "Hive Demo",
            title = "Velvet Echo",
            genre = "R&B",
            bpm = 96,
            priceRubles = 2490,
            coverImageFileName = "velvet-echo-cover.jpg",
            audioPreviewUrl = null,
            coverImageUrl = null,
            description = "Мягкий мелодичный грув для вокальных партий и атмосферных куплетов.",
            plays = 76,
        ),
    )
}
