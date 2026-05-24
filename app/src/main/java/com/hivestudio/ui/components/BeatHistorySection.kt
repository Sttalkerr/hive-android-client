package com.hivestudio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.model.BeatHistoryPointUi
import com.hivestudio.ui.theme.BlueAccent
import com.hivestudio.ui.theme.BlueAccentSoft
import com.hivestudio.ui.theme.GraphiteSoft

@Composable
fun BeatHistorySection(
    history: List<BeatHistoryPointUi>,
    modifier: Modifier = Modifier,
) {
    val maxPlays = history.maxOfOrNull { it.playsCount }?.coerceAtLeast(1) ?: 1

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Динамика за 7 дней",
                style = MaterialTheme.typography.titleMedium,
            )

            history.forEach { point ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(point.dateLabel, style = MaterialTheme.typography.labelLarge)
                        Text(
                            "${point.playsCount} / ${point.likesCount} / ${point.purchasesCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(GraphiteSoft),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = point.playsCount.toFloat() / maxPlays.toFloat())
                                .height(10.dp)
                                .background(BlueAccent),
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Прослушивания: ${point.playsCount}", style = MaterialTheme.typography.bodySmall)
                        Text("Лайки: ${point.likesCount}", style = MaterialTheme.typography.bodySmall, color = BlueAccentSoft)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Покупки: ${point.purchasesCount}", style = MaterialTheme.typography.bodySmall)
                        Text("Выручка: ${point.revenueRubles} ₽", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
