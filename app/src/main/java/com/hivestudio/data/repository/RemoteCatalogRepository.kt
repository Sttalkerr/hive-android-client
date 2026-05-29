package com.hivestudio.data.repository

import com.hivestudio.data.remote.HiveStudioApi
import com.hivestudio.data.remote.ApiConfig
import com.hivestudio.data.remote.HiveStudioApiFactory
import com.hivestudio.data.remote.model.BeatDto
import com.hivestudio.data.remote.model.BeatHistoryPointDto
import com.hivestudio.data.remote.model.BeatStatisticsDto
import com.hivestudio.data.remote.model.UpdateBeatRequestDto
import com.hivestudio.data.session.SessionStore
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.BeatDetailsUi
import com.hivestudio.ui.model.BeatHistoryPointUi
import com.hivestudio.ui.model.DashboardOverviewUi
import com.hivestudio.ui.model.DashboardMetricUi
import com.hivestudio.ui.model.MetricTrendPointUi
import com.hivestudio.ui.model.MetricTrendUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

class RemoteCatalogRepository(
    private val api: HiveStudioApi = HiveStudioApiFactory.create(),
) {
    suspend fun loadBeatCards(query: String?): List<BeatCardUi> = coroutineScope {
        if (query == null) {
            ClientDataCache.ownBeatCards?.let { return@coroutineScope it }
        }
        api.getBeats(query).map { beat ->
            beat.toBeatCardUi()
        }.also {
            if (query == null) {
                ClientDataCache.ownBeatCards = it
            }
        }
    }

    suspend fun loadCatalogBeatCards(query: String?): List<BeatCardUi> = coroutineScope {
        if (query == null) {
            ClientDataCache.publicCatalogBeatCards?.let { return@coroutineScope it }
        }
        api.getCatalogBeats(query).map { beat ->
            beat.toBeatCardUi()
        }.also {
            if (query == null) {
                ClientDataCache.publicCatalogBeatCards = it
            }
        }
    }

    suspend fun loadDashboardMetrics(): List<DashboardMetricUi> {
        val beats = loadBeatCards(query = null)

        val totalPlays = beats.sumOf { it.plays }
        val totalLikes = beats.sumOf { it.likes }
        val totalPurchases = beats.sumOf { it.purchases }
        val totalRevenue = beats.sumOf { it.revenueRubles }

        return listOf(
            DashboardMetricUi(AnalyticsMetricType.Plays, "Прослушивания", totalPlays.toString()),
            DashboardMetricUi(AnalyticsMetricType.Likes, "Лайки", totalLikes.toString()),
            DashboardMetricUi(AnalyticsMetricType.Purchases, "Покупки", totalPurchases.toString()),
            DashboardMetricUi(AnalyticsMetricType.Revenue, "Выручка", formatRubles(totalRevenue)),
        )
    }

    suspend fun loadDashboardOverview(): DashboardOverviewUi {
        ClientDataCache.dashboardOverview?.let { return it }
        val beatCards = loadBeatCards(query = null)
        val metrics = loadDashboardMetrics()
        return DashboardOverviewUi(
            metrics = metrics,
            beatsCount = beatCards.size,
            topBeat = beatCards.maxByOrNull { it.plays },
            recentBeats = beatCards.sortedByDescending { it.plays }.take(3),
        ).also { ClientDataCache.dashboardOverview = it }
    }

    suspend fun deleteBeat(beatId: String) {
        api.deleteBeat(beatId)
        ClientDataCache.clearAll()
    }

    suspend fun updateBeat(
        beatId: String,
        title: String,
        genre: String,
        bpm: Int,
        priceRubles: Int,
        description: String,
    ): BeatCardUi {
        val beat = api.updateBeat(
            beatId = beatId,
            request = UpdateBeatRequestDto(
                title = title.trim(),
                genre = genre.trim(),
                bpm = bpm,
                price = priceRubles.toDouble(),
                description = description.trim(),
            )
        )
        val stats = api.getStatistics(beat.id)
        return beat.toBeatCardUi(stats).also {
            ClientDataCache.clearAll()
        }
    }

    suspend fun loadBeatDetails(beatId: String): BeatDetailsUi = coroutineScope {
        val beatDeferred = async { api.getCatalogBeat(beatId) }
        val statsDeferred = async { api.getCatalogStatistics(beatId) }
        val historyDeferred = async { api.getCatalogHistory(beatId) }

        val beat = beatDeferred.await()
        val stats = statsDeferred.await()
        val history = historyDeferred.await()

        BeatDetailsUi(
            beat = beat.toBeatCardUi(stats),
            likesCount = stats.likesCount,
            purchasesCount = stats.purchasesCount,
            revenueRubles = stats.revenueTotal.roundToInt(),
            updatedAt = stats.updatedAt,
            history = history.map { it.toBeatHistoryPointUi() },
        )
    }

    suspend fun simulatePlay(beatId: String) {
        api.simulatePlay(beatId)
        ClientDataCache.clearAll()
    }

    suspend fun simulateLike(beatId: String) {
        api.simulateLike(beatId)
        ClientDataCache.clearAll()
    }

    suspend fun simulatePurchase(beatId: String) {
        api.simulatePurchase(beatId)
        ClientDataCache.clearAll()
    }

    suspend fun warmUpSessionData() {
        loadBeatCards(query = null)
        loadDashboardOverview()
    }

    suspend fun loadMetricTrend(metricType: AnalyticsMetricType): MetricTrendUi = coroutineScope {
        val beats = api.getBeats()
        val histories = beats.map { beat ->
            async { api.getHistory(beat.id) }
        }.awaitAll()

        val aggregated = linkedMapOf<String, AggregatePoint>()
        histories.flatten().forEach { point ->
            val current = aggregated.getOrPut(point.date) { AggregatePoint() }
            current.plays += point.playsCount
            current.likes += point.likesCount
            current.purchases += point.purchasesCount
            current.revenue += point.revenueTotal
        }

        val points = aggregated.entries.map { (date, point) ->
            MetricTrendPointUi(
                label = date.substringAfterLast("-"),
                value = point.valueFor(metricType).toFloat(),
                formattedValue = point.formattedValueFor(metricType),
            )
        }
        val totalNumeric = points.sumOf { it.value.toInt() }

        MetricTrendUi(
            type = metricType,
            title = metricType.title,
            totalValue = when (metricType) {
                AnalyticsMetricType.Revenue -> RubleFormatter.format(points.sumOf { it.formattedValue.removeSuffix(" ₽").replace(" ", "").toIntOrNull() ?: 0 })
                else -> totalNumeric.toString()
            },
            points = points,
        )
    }

    private fun formatRubles(amount: Int): String = RubleFormatter.format(amount)

    private fun String.toAbsoluteApiUrl(): String =
        if (startsWith("http://") || startsWith("https://")) this else "${ApiConfig.BASE_URL}${removePrefix("/")}"

    private fun BeatDto.toBeatCardUi(): BeatCardUi =
        BeatCardUi(
            id = id,
            producerId = producerId,
            producerStageName = producerStageName,
            producerAvatarUrl = producerAvatarUrl.toAbsoluteApiUrlOrNull(),
            isOwnedBySession = producerId == SessionStore.currentProducerId,
            title = title,
            genre = genre,
            bpm = bpm,
            priceRubles = price.roundToInt(),
            coverImageFileName = coverImageFileName,
            audioPreviewUrl = mp3Url.toAbsoluteApiUrl(),
            coverImageUrl = coverImageUrl.toAbsoluteApiUrl(),
            description = description,
            plays = playsCount,
            likes = likesCount,
            purchases = purchasesCount,
            revenueRubles = revenueTotal.roundToInt(),
        )

    private fun BeatDto.toBeatCardUi(
        stats: BeatStatisticsDto,
    ): BeatCardUi =
        toBeatCardUi().copy(
            plays = stats.playsCount,
            likes = stats.likesCount,
            purchases = stats.purchasesCount,
            revenueRubles = stats.revenueTotal.roundToInt(),
        )

    private fun BeatHistoryPointDto.toBeatHistoryPointUi(): BeatHistoryPointUi =
        BeatHistoryPointUi(
            dateLabel = date.substringAfterLast("-"),
            playsCount = playsCount,
            likesCount = likesCount,
            purchasesCount = purchasesCount,
            revenueRubles = revenueTotal.roundToInt(),
        )

    private fun String?.toAbsoluteApiUrlOrNull(): String? =
        this?.takeIf { it.isNotBlank() }?.toAbsoluteApiUrl()

    private class AggregatePoint(
        var plays: Int = 0,
        var likes: Int = 0,
        var purchases: Int = 0,
        var revenue: Double = 0.0,
    ) {
        fun valueFor(metricType: AnalyticsMetricType): Int =
            when (metricType) {
                AnalyticsMetricType.Plays -> plays
                AnalyticsMetricType.Likes -> likes
                AnalyticsMetricType.Purchases -> purchases
                AnalyticsMetricType.Revenue -> revenue.roundToInt()
            }

        fun formattedValueFor(metricType: AnalyticsMetricType): String =
            when (metricType) {
                AnalyticsMetricType.Revenue -> RubleFormatter.format(revenue.roundToInt())
                else -> valueFor(metricType).toString()
            }
    }
}
