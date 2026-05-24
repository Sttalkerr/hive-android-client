package com.hivestudio.data.remote.model

data class ProfileDto(
    val id: String,
    val email: String,
    val stageName: String,
    val bio: String,
    val city: String,
    val contactTag: String,
    val avatarUrl: String?,
    val beatsCount: Int,
    val totalPlays: Int,
    val totalRevenue: Double,
    val createdAt: String,
)
