package com.hivestudio.ui.model

data class BeatCardUi(
    val id: String,
    val producerId: String,
    val producerStageName: String,
    val producerAvatarUrl: String? = null,
    val isOwnedBySession: Boolean = false,
    val title: String,
    val genre: String,
    val bpm: Int,
    val priceRubles: Int,
    val coverImageFileName: String,
    val audioPreviewUrl: String? = null,
    val coverImageUrl: String? = null,
    val localCoverUri: String? = null,
    val description: String,
    val plays: Int,
    val likes: Int = 0,
    val purchases: Int = 0,
    val revenueRubles: Int = 0,
)
