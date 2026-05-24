package com.hivestudio.data.remote.model

data class AuthRequestDto(
    val email: String,
    val password: String,
    val stageName: String? = null,
)

data class AuthResponseDto(
    val id: String,
    val email: String,
    val stageName: String,
    val avatarUrl: String?,
    val token: String,
)
