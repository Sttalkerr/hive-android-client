package com.hivestudio.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.DashboardHeroPanel
import com.hivestudio.ui.components.DashboardInfoChip
import com.hivestudio.ui.components.DashboardMetricCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.components.TopBeatPanel
import com.hivestudio.ui.model.LoadState

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onOpenBeat: (String) -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Hive Studio",
                subtitle = "Статистика каталога, лидеры по интересу и быстрый срез по монетизации.",
            )
        }

        when (val current = state) {
            LoadState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> {
                item {
                    Text(current.message)
                }
            }

            is LoadState.Success -> {
                item {
                    DashboardHeroPanel(overview = current.data)
                }

                current.data.topBeat?.let { beat ->
                    item {
                        TopBeatPanel(
                            beat = beat,
                            onOpenClick = { onOpenBeat(beat.id) },
                        )
                    }
                }

                item {
                    DashboardInfoChip(
                        label = "Сильнейшие позиции каталога",
                        value = current.data.recentBeats.joinToString(" • ") { it.title }.ifBlank { "Каталог пока пуст" },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                items(current.data.metrics.chunked(2)) { row ->
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        row.forEachIndexed { index, metric ->
                            DashboardMetricCard(
                                metric = metric,
                                modifier = Modifier.weight(1f),
                                emphasized = current.data.metrics.indexOf(metric) == 0 && index == 0,
                            )
                        }
                        if (row.size == 1) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
