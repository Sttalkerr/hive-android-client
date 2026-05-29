package com.hivestudio.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.DashboardMetricCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.components.TopBeatPanel
import com.hivestudio.ui.model.LoadState

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onOpenBeat: (String) -> Unit = {},
    onOpenMetric: (String) -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title = "Hive Studio",
                subtitle = "Расширенная статистика",
            )
        }

        when (val current = state) {
            LoadState.Loading -> item { }
            is LoadState.Error -> item { Text(current.message) }
            is LoadState.Success -> {
                current.data.topBeat?.let { beat ->
                    item {
                        TopBeatPanel(
                            beat = beat,
                            onOpenClick = { onOpenBeat(beat.id) },
                        )
                    }
                }

                items(current.data.metrics.chunked(2)) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        row.forEach { metric ->
                            DashboardMetricCard(
                                metric = metric,
                                modifier = Modifier.weight(1f),
                                emphasized = metric == current.data.metrics.firstOrNull(),
                                onClick = { onOpenMetric(metric.type.routeKey) },
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
