package com.hivestudio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.theme.BlueAccent
import com.hivestudio.ui.theme.BlueAccentSoft
import com.hivestudio.ui.theme.GraphiteSoft

@Composable
fun TopBeatPanel(
    beat: BeatCardUi,
    modifier: Modifier = Modifier,
    onOpenClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = BlueAccent.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Топ-бит недели", style = MaterialTheme.typography.labelLarge, color = BlueAccentSoft)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(22.dp)),
                ) {
                    BeatArtwork(beat = beat, modifier = Modifier.matchParentSize())
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        beat.producerStageName,
                        style = MaterialTheme.typography.labelLarge,
                        color = BlueAccentSoft,
                    )
                    Text(
                        beat.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
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
                        "${beat.plays} прослушиваний",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BlueAccentSoft,
                    )
                }
            }
            TextButton(onClick = onOpenClick) {
                Text("Открыть аналитику бита")
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
        shape = RoundedCornerShape(26.dp),
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
                        .clip(RoundedCornerShape(18.dp)),
                ) {
                    BeatArtwork(beat = beat, modifier = Modifier.matchParentSize())
                }
                Column(
                    modifier = Modifier.weight(0.66f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        beat.producerStageName,
                        style = MaterialTheme.typography.labelLarge,
                        color = BlueAccentSoft,
                    )
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
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
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
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteSoft),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("События", style = MaterialTheme.typography.titleMedium)
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
            if (onDelete != null) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Удалить бит")
                }
            }
        }
    }
}
