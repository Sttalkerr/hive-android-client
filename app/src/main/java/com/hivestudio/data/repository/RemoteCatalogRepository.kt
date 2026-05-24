package com.hivestudio.data.repository

import com.hivestudio.data.remote.HiveStudioApi
import com.hivestudio.data.remote.ApiConfig
import com.hivestudio.data.remote.HiveStudioApiFactory
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.DashboardMetricUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

class RemoteCatalogRepository(
    private val api: HiveStudioApi = HiveStudioApiFactory.create(),
) {
    suspend fun loadBeatCards(query: String?): List<BeatCardUi> = coroutineScope {
        val beats = api.getBeats(query)
        beats.map { beat ->
            async {
                val stats = api.getStatistics(beat.id)
                BeatCardUi(
                    id = beat.id,
                    title = beat.title,
                    genre = beat.genre,
                    bpm = beat.bpm,
                    priceRubles = beat.price.roundToInt(),
                    coverImageFileName = beat.coverImageFileName,
                    coverImageUrl = "${ApiConfig.BASE_URL}uploads/${beat.coverImageFileName}",
                    description = beat.description,
                    plays = stats.playsCount,
                )
            }
        }.awaitAll()
    }

    suspend fun loadDashboardMetrics(): List<DashboardMetricUi> = coroutineScope {
        val beats = api.getBeats()
        val statsList = beats.map { beat ->
            async { api.getStatistics(beat.id) }
        }.awaitAll()

        val totalPlays = statsList.sumOf { it.playsCount }
        val totalLikes = statsList.sumOf { it.likesCount }
        val totalPurchases = statsList.sumOf { it.purchasesCount }
        val totalRevenue = statsList.sumOf { it.revenueTotal }.roundToInt()

        listOf(
            DashboardMetricUi("Прослушивания", totalPlays.toString(), "Сумма по всем битам"),
            DashboardMetricUi("Лайки", totalLikes.toString(), "Реальные данные с сервера"),
            DashboardMetricUi("Покупки", totalPurchases.toString(), "События simulate/purchase"),
            DashboardMetricUi("Выручка", formatRubles(totalRevenue), "Расчёт по серверной статистике"),
        )
    }

    suspend fun deleteBeat(beatId: String) {
        api.deleteBeat(beatId)
    }

    private fun formatRubles(amount: Int): String = "${amount.toString().reversed().chunked(3).joinToString(" ").reversed()} ₽"
}
