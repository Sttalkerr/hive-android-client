package com.hivestudio.data.remote

import com.hivestudio.data.remote.model.AuthRequestDto
import com.hivestudio.data.remote.model.AuthResponseDto
import com.hivestudio.data.remote.model.BeatDto
import com.hivestudio.data.remote.model.BeatHistoryPointDto
import com.hivestudio.data.remote.model.BeatStatisticsDto
import com.hivestudio.data.remote.model.ProfileDto
import com.hivestudio.data.remote.model.UpdateProfileRequestDto
import com.hivestudio.data.remote.model.SimulationResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @PUT("api/v1/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequestDto,
    ): ProfileDto

    @Multipart
    @POST("api/v1/profile/avatar")
    suspend fun uploadAvatar(
        @Part avatar: MultipartBody.Part,
    ): ProfileDto

    @GET("api/v1/catalog/beats")
    suspend fun getCatalogBeats(
        @Query("query") query: String? = null,
    ): List<BeatDto>

    @GET("api/v1/catalog/beats/{beatId}")
    suspend fun getCatalogBeat(
        @Path("beatId") beatId: String,
    ): BeatDto

    @GET("api/v1/beats")
    suspend fun getBeats(
        @Query("query") query: String? = null,
    ): List<BeatDto>

    @GET("api/v1/beats/{beatId}")
    suspend fun getBeat(
        @Path("beatId") beatId: String,
    ): BeatDto

    @DELETE("api/v1/beats/{beatId}")
    suspend fun deleteBeat(
        @Path("beatId") beatId: String,
    )

    @Multipart
    @POST("api/v1/beats")
    suspend fun createBeat(
        @Part("title") title: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("bpm") bpm: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part mp3: MultipartBody.Part,
        @Part coverImage: MultipartBody.Part,
    ): BeatDto

    @GET("api/v1/beats/{beatId}/stats")
    suspend fun getStatistics(
        @Path("beatId") beatId: String,
    ): BeatStatisticsDto

    @GET("api/v1/catalog/beats/{beatId}/stats")
    suspend fun getCatalogStatistics(
        @Path("beatId") beatId: String,
    ): BeatStatisticsDto

    @GET("api/v1/beats/{beatId}/history")
    suspend fun getHistory(
        @Path("beatId") beatId: String,
        @Query("days") days: Int = 7,
    ): List<BeatHistoryPointDto>

    @GET("api/v1/catalog/beats/{beatId}/history")
    suspend fun getCatalogHistory(
        @Path("beatId") beatId: String,
        @Query("days") days: Int = 7,
    ): List<BeatHistoryPointDto>

    @POST("api/v1/beats/{beatId}/simulate/play")
    suspend fun simulatePlay(
        @Path("beatId") beatId: String,
    ): SimulationResponseDto

    @POST("api/v1/beats/{beatId}/simulate/like")
    suspend fun simulateLike(
        @Path("beatId") beatId: String,
    ): SimulationResponseDto

    @POST("api/v1/beats/{beatId}/simulate/purchase")
    suspend fun simulatePurchase(
        @Path("beatId") beatId: String,
    ): SimulationResponseDto
}
