package com.hivestudio.data.remote.model

data class BeatDto(
    val id: String,
    val title: String,
    val genre: String,
    val bpm: Int,
    val price: Double,
    val description: String,
    val mp3FileName: String,
    val mp3Url: String,
    val coverImageFileName: String,
    val coverImageUrl: String,
    val createdAt: String,
)

data class CreateBeatRequestDto(
    val title: String,
    val genre: String,
    val bpm: Int,
    val price: Double,
    val description: String,
    val mp3FileName: String,
    val coverImageFileName: String,
)
