package com.hivestudio.ui.screens.metricdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.model.LoadState
import com.hivestudio.ui.theme.BlueAccent
import com.hivestudio.ui.theme.GraphiteSoft

@Composable
fun MetricDetailsScreen(
    metricType: AnalyticsMetricType,
    viewModel: MetricDetailsViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(metricType) {
        viewModel.load(metricType)
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title = metricType.title,
            )
        }

        when (val current = state) {
            LoadState.Loading -> item { }
            is LoadState.Error -> item { Text(current.message) }
            is LoadState.Success -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GraphiteSoft),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            Text(
                                current.data.totalValue,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                val maxValue = current.data.points.maxOfOrNull { it.value }?.coerceAtLeast(1f) ?: 1f
                                current.data.points.forEach { point ->
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            point.formattedValue,
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
                                                    .height((120f * point.value / maxValue).coerceAtLeast(10f).dp)
                                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                                    .background(BlueAccent),
                                            )
                                        }
                                        Text(point.label, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
