package com.hivestudio.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun ProfileAvatar(
    stageName: String,
    avatarUrl: String?,
    localAvatarUri: String? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val avatar = produceState<ImageBitmap?>(initialValue = null, avatarUrl, localAvatarUri) {
        value = withContext(Dispatchers.IO) {
            try {
                when {
                    !localAvatarUri.isNullOrBlank() -> {
                        context.contentResolver.openInputStream(Uri.parse(localAvatarUri))?.use { input ->
                            BitmapFactory.decodeStream(input)?.asImageBitmap()
                        }
                    }
                    !avatarUrl.isNullOrBlank() -> {
                        URL(avatarUrl).openStream().use { input ->
                            BitmapFactory.decodeStream(input)?.asImageBitmap()
                        }
                    }
                    else -> null
                }
            } catch (_: Exception) {
                null
            }
        }
    }.value

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (avatar != null) {
            Image(
                bitmap = avatar,
                contentDescription = stageName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = stageName.take(1).uppercase().ifBlank { "H" },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
