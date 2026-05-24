package com.hivestudio.data.remote.model

data class UpdateProfileRequestDto(
    val stageName: String,
    val bio: String,
    val city: String,
    val contactTag: String,
)
