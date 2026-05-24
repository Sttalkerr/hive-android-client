package com.hivestudio.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.hivestudio.data.remote.HiveStudioApi
import com.hivestudio.data.remote.HiveStudioApiFactory
import com.hivestudio.data.remote.model.BeatDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddBeatUploadRepository(
    private val api: HiveStudioApi = HiveStudioApiFactory.create(),
) {
    suspend fun uploadBeat(
        context: Context,
        draft: com.hivestudio.ui.model.AddBeatDraftUi,
        mp3Uri: Uri,
        coverUri: Uri,
    ): BeatDto {
        val title = draft.title.trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val genre = draft.genre.trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val bpm = draft.bpm.toRequestBody("text/plain".toMediaTypeOrNull())
        val price = draft.priceRubles.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = draft.description.trim().toRequestBody("text/plain".toMediaTypeOrNull())

        val mp3Part = context.contentResolver.toMultipartPart(
            uri = mp3Uri,
            formField = "mp3",
            fallbackName = "beat.mp3",
            mediaType = "audio/mpeg",
        )
        val coverPart = context.contentResolver.toMultipartPart(
            uri = coverUri,
            formField = "coverImage",
            fallbackName = "cover.jpg",
            mediaType = "image/*",
        )

        return api.createBeat(
            title = title,
            genre = genre,
            bpm = bpm,
            price = price,
            description = description,
            mp3 = mp3Part,
            coverImage = coverPart,
        )
    }
}
