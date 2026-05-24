package com.hivestudio.ui.model

data class BeatDetailsUi(
    val beat: BeatCardUi,
    val likesCount: Int,
    val purchasesCount: Int,
    val revenueRubles: Int,
    val updatedAt: String,
    val history: List<BeatHistoryPointUi>,
)
