package com.hivestudio.data.repository

import com.hivestudio.data.remote.HiveStudioApi
import com.hivestudio.data.remote.HiveStudioApiFactory
import com.hivestudio.data.remote.model.AuthRequestDto
import com.hivestudio.data.remote.model.AuthResponseDto
import com.hivestudio.data.remote.model.ProfileDto

class AuthRepository(
    private val api: HiveStudioApi = HiveStudioApiFactory.create(),
) {
    suspend fun register(
        email: String,
        password: String,
        stageName: String,
    ): AuthResponseDto =
        api.register(
            AuthRequestDto(
                email = email.trim(),
                password = password,
                stageName = stageName.trim(),
            )
        )

    suspend fun login(
        email: String,
        password: String,
    ): AuthResponseDto =
        api.login(
            AuthRequestDto(
                email = email.trim(),
                password = password,
            )
        )

    suspend fun loadProfile(): ProfileDto = api.getProfile()
}
