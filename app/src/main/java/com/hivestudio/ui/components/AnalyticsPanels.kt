package com.hivestudio.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.DashboardOverviewUi
import com.hivestudio.ui.theme.BlueAccent
import com.hivestudio.ui.theme.BlueAccentSoft
import com.hivestudio.ui.theme.GraphiteSoft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun DashboardHeroPanel(
    overview: DashboardOverviewUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GraphiteSoft),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Hive Studio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Центр аналитики по твоим битам: выручка, конверсия, пики интереса и лидеры каталога.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DashboardInfoChip(
                    label = "Битов в каталоге",
                    value = overview.beatsCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                DashboardInfoChip(
                    label = "Лидер по plays",
                    value = overview.topBeat?.title ?: "Нет данных",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun DashboardInfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BlueAccent.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TopBeatPanel(
    beat: BeatCardUi,
    modifier: Modifier = Modifier,
    onOpenClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlueAccent.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Топ-бит недели", style = MaterialTheme.typography.titleMedium)
            Text(beat.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "${beat.genre} • ${beat.bpm} BPM • ${RubleFormatter.format(beat.priceRubles)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Прослушивания: ${beat.plays}",
                style = MaterialTheme.typography.bodyLarge,
            )
            OutlinedButton(onClick = onOpenClick) {
                Text("Открыть карточку")
            }
        }
    }
}

@Composable
fun BeatDetailsHeroPanel(
    beat: BeatCardUi,
    previewButtonText: String,
    onPreviewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GraphiteSoft),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.34f)
                        .height(124.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BlueAccent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    BeatCardArtwork(beat = beat)
                }
                Column(
                    modifier = Modifier.weight(0.66f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(beat.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "${beat.genre} • ${beat.bpm} BPM",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        RubleFormatter.format(beat.priceRubles),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        beat.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            OutlinedButton(onClick = onPreviewClick, modifier = Modifier.fillMaxWidth()) {
                Text(previewButtonText)
            }
        }
    }
}

@Composable
fun BeatQuickActionsPanel(
    onPlay: () -> Unit,
    onLike: () -> Unit,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GraphiteSoft),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Быстрые события", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(onClick = onPlay, modifier = Modifier.weight(1f)) {
                    Text("Play")
                }
                Button(onClick = onLike, modifier = Modifier.weight(1f)) {
                    Text("Like")
                }
                Button(onClick = onPurchase, modifier = Modifier.weight(1f)) {
                    Text("Sale")
                }
            }
            Text(
                "Каждое действие сразу меняет историю по дням и итоговые метрики.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BeatCardArtwork(
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

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                color = BlueAccentSoft,
            )
        }
    }
}
