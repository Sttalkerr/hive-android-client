package com.hivestudio.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun ContentResolver.toMultipartPart(
    uri: Uri,
    formField: String,
    fallbackName: String,
    mediaType: String,
): MultipartBody.Part {
    val displayName = queryDisplayName(uri) ?: fallbackName
    val tempFile = File.createTempFile("hive-upload-", displayName.substringAfterLast('.', "tmp"))

    openInputStream(uri)?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    } ?: throw IllegalArgumentException("Cannot open selected file: $uri")

    val requestBody: RequestBody = tempFile.asRequestBody(mediaType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(formField, displayName, requestBody)
}

private fun ContentResolver.queryDisplayName(uri: Uri): String? {
    query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) {
            return cursor.getString(index)
        }
    }
    return null
}
