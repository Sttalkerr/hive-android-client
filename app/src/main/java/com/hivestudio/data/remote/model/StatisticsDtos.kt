package com.hivestudio.data.remote.model

data class BeatStatisticsDto(
    val beatId: String,
    val playsCount: Int,
    val likesCount: Int,
    val purchasesCount: Int,
    val revenueTotal: Double,
    val updatedAt: String,
)

data class SimulationResponseDto(
    val beatId: String,
    val eventType: String,
    val message: String,
)

data class BeatHistoryPointDto(
    val date: String,
    val playsCount: Int,
    val likesCount: Int,
    val purchasesCount: Int,
    val revenueTotal: Double,
)
