package com.hivestudio.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.BeatCardUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun BeatCard(
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

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
                        text = beat.coverImageFileName.ifBlank { "Обложка не загружена" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = beat.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = beat.genre, style = MaterialTheme.typography.bodyMedium)
                Text(text = "${beat.bpm} BPM", style = MaterialTheme.typography.bodyMedium)
                Text(text = RubleFormatter.format(beat.priceRubles), style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = beat.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Прослушивания: ${beat.plays}",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
