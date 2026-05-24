package com.hivestudio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.model.BeatHistoryPointUi
import com.hivestudio.ui.theme.BlueAccent
import com.hivestudio.ui.theme.BlueAccentSoft
import com.hivestudio.ui.theme.GraphiteSoft

@Composable
fun BeatHistorySection(
    history: List<BeatHistoryPointUi>,
    modifier: Modifier = Modifier,
) {
    var selectedMetric by remember { mutableStateOf(AnalyticsMetricType.Plays) }
    val values = history.map { point -> point.valueFor(selectedMetric) }
    val maxValue = values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val totalValue = values.sum()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteSoft),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Динамика за 7 дней", style = MaterialTheme.typography.titleMedium)
                    Text(
                        when (selectedMetric) {
                            AnalyticsMetricType.Revenue -> RubleFormatter.format(totalValue)
                            else -> totalValue.toString()
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    selectedMetric.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = BlueAccentSoft,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnalyticsMetricType.entries.forEach { metric ->
                    MetricFilterChip(
                        label = metric.shortLabel,
                        selected = metric == selectedMetric,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedMetric = metric },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                history.forEach { point ->
                    val value = point.valueFor(selectedMetric)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = point.displayValueFor(selectedMetric),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((100f * value / maxValue).coerceAtLeast(8f).dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedMetric == AnalyticsMetricType.Revenue) BlueAccentSoft else BlueAccent),
                            )
                        }
                        Text(
                            text = point.dateLabel,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricFilterChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) BlueAccent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) BlueAccentSoft else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun BeatHistoryPointUi.valueFor(metric: AnalyticsMetricType): Int =
    when (metric) {
        AnalyticsMetricType.Plays -> playsCount
        AnalyticsMetricType.Likes -> likesCount
        AnalyticsMetricType.Purchases -> purchasesCount
        AnalyticsMetricType.Revenue -> revenueRubles
    }

private fun BeatHistoryPointUi.displayValueFor(metric: AnalyticsMetricType): String =
    when (metric) {
        AnalyticsMetricType.Revenue -> RubleFormatter.format(revenueRubles)
        else -> valueFor(metric).toString()
    }
