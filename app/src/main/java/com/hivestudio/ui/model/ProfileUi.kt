package com.hivestudio.ui.model

data class ProfileUi(
    val id: String,
    val email: String,
    val stageName: String,
    val bio: String,
    val city: String,
    val contactTag: String,
    val avatarUrl: String?,
    val beatsCount: Int,
    val totalPlays: Int,
    val totalRevenue: Int,
    val createdAt: String,
)
