package com.hivestudio.ui.screens.beatdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.components.BeatHistorySection
import com.hivestudio.ui.components.BeatDetailsHeroPanel
import com.hivestudio.ui.components.BeatQuickActionsPanel
import com.hivestudio.ui.components.DashboardMetricCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.media.AudioPreviewPlayer
import com.hivestudio.ui.model.DashboardMetricUi
import com.hivestudio.ui.model.LoadState

@Composable
fun BeatDetailsScreen(
    beatId: String,
    viewModel: BeatDetailsViewModel = viewModel(),
    onEdit: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val player = remember(beatId) { AudioPreviewPlayer(context) }

    LaunchedEffect(beatId) {
        viewModel.loadBeat(beatId)
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Аналитика бита",
                subtitle = "Подробная карточка релиза, история интереса и тестовые действия по статистике.",
            )
        }

        when (val current = state) {
            LoadState.Loading -> item { CircularProgressIndicator() }
            is LoadState.Error -> item { Text(current.message) }
            is LoadState.Success -> {
                item {
                    BeatDetailsHeroPanel(
                        beat = current.data.beat,
                        previewButtonText = when {
                            player.isLoading -> "Загрузка превью..."
                            player.isPlaying -> "Остановить превью"
                            else -> "Слушать MP3-превью"
                        },
                        onPreviewClick = { player.togglePlayback(current.data.beat.audioPreviewUrl) },
                    )
                }

                if (!player.errorMessage.isNullOrBlank()) {
                    item {
                        Text(player.errorMessage!!)
                    }
                }

                item {
                    BeatHistorySection(history = current.data.history)
                }

                val metrics = listOf(
                    DashboardMetricUi(AnalyticsMetricType.Plays, "Прослушивания", current.data.beat.plays.toString()),
                    DashboardMetricUi(AnalyticsMetricType.Likes, "Лайки", current.data.likesCount.toString()),
                    DashboardMetricUi(AnalyticsMetricType.Purchases, "Покупки", current.data.purchasesCount.toString()),
                    DashboardMetricUi(AnalyticsMetricType.Revenue, "Выручка", "${current.data.revenueRubles} ₽"),
                )

                items(metrics.chunked(2).size) { rowIndex ->
                    val row = metrics.chunked(2)[rowIndex]
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEachIndexed { index, metric ->
                            DashboardMetricCard(
                                metric = metric,
                                modifier = Modifier.weight(1f),
                                emphasized = rowIndex == 0 && index == 0,
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                if (current.data.beat.isOwnedBySession) {
                    item {
                        BeatQuickActionsPanel(
                            onPlay = { viewModel.simulatePlay(beatId) },
                            onLike = { viewModel.simulateLike(beatId) },
                            onPurchase = { viewModel.simulatePurchase(beatId) },
                            onEdit = { onEdit(beatId) },
                            onDelete = { viewModel.deleteBeat(beatId, onDeleted) },
                        )
                    }
                }
            }
        }
    }
}
