package com.hivestudio.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.hivestudio.ui.model.BeatCardUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun BeatArtwork(
    beat: BeatCardUi,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val artwork by produceState<ImageBitmap?>(initialValue = null, beat.coverImageUrl, beat.localCoverUri) {
        value = withContext(Dispatchers.IO) {
            try {
                when {
                    !beat.localCoverUri.isNullOrBlank() -> {
                        context.contentResolver.openInputStream(Uri.parse(beat.localCoverUri))?.use { input ->
                            BitmapFactory.decodeStream(input)?.asImageBitmap()
                        }
                    }
                    !beat.coverImageUrl.isNullOrBlank() -> {
                        URL(beat.coverImageUrl).openStream().use { input ->
                            BitmapFactory.decodeStream(input)?.asImageBitmap()
                        }
                    }
                    else -> null
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (artwork != null) {
            Image(
                bitmap = artwork!!,
                contentDescription = beat.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = beat.coverImageFileName.ifBlank { "Cover" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
