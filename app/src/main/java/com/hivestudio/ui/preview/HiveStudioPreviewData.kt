package com.hivestudio.ui.preview

import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.DashboardMetricUi

object HiveStudioPreviewData {
    val dashboardMetrics = listOf(
        DashboardMetricUi("Прослушивания", "200", "За последние 7 дней"),
        DashboardMetricUi("Лайки", "58", "Рост интереса к каталогу"),
        DashboardMetricUi("Покупки", "13", "Учебные покупки лицензий"),
        DashboardMetricUi("Выручка", "$364.87", "Сумма по всем битам"),
    )

    val beats = listOf(
        BeatCardUi(
            title = "Midnight Pulse",
            genre = "Trap",
            bpm = 140,
            price = "$29.99",
            description = "Темный ударный бит для ночного вайба и плотного речитатива.",
            plays = 124,
        ),
        BeatCardUi(
            title = "Velvet Echo",
            genre = "R&B",
            bpm = 96,
            price = "$24.99",
            description = "Мягкий мелодичный грув для вокальных партий и атмосферных куплетов.",
            plays = 76,
        ),
    )
}
