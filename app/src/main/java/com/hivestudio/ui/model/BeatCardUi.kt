package com.hivestudio.ui.model

data class BeatCardUi(
    val id: String,
    val title: String,
    val genre: String,
    val bpm: Int,
    val priceRubles: Int,
    val coverImageFileName: String,
    val coverImageUrl: String? = null,
    val localCoverUri: String? = null,
    val description: String,
    val plays: Int,
)
