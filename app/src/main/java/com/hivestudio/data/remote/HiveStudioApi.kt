package com.hivestudio.data.remote

import com.hivestudio.data.remote.model.AuthRequestDto
import com.hivestudio.data.remote.model.AuthResponseDto
import com.hivestudio.data.remote.model.BeatDto
import com.hivestudio.data.remote.model.BeatStatisticsDto
import com.hivestudio.data.remote.model.CreateBeatRequestDto
import com.hivestudio.data.remote.model.ProfileDto
import com.hivestudio.data.remote.model.SimulationResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HiveStudioApi {
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: AuthRequestDto,
    ): AuthResponseDto

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: AuthRequestDto,
    ): AuthResponseDto

    @GET("api/v1/profile")
    suspend fun getProfile(): ProfileDto

    @GET("api/v1/beats")
    suspend fun getBeats(
        @Query("query") query: String? = null,
    ): List<BeatDto>

    @POST("api/v1/beats")
    suspend fun createBeat(
        @Body request: CreateBeatRequestDto,
    ): BeatDto

    @GET("api/v1/beats/{beatId}/stats")
    suspend fun getStatistics(
        @Path("beatId") beatId: String,
    ): BeatStatisticsDto

    @POST("api/v1/beats/{beatId}/simulate/purchase")
    suspend fun simulatePurchase(
        @Path("beatId") beatId: String,
    ): SimulationResponseDto
}
