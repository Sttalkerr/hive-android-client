package com.hivestudio.ui.screens.beatdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.BeatCard
import com.hivestudio.ui.components.DashboardMetricCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.media.AudioPreviewPlayer
import com.hivestudio.ui.model.DashboardMetricUi
import com.hivestudio.ui.model.LoadState
import androidx.compose.runtime.remember

@Composable
fun BeatDetailsScreen(
    beatId: String,
    viewModel: BeatDetailsViewModel = viewModel(),
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
                title = "Карточка бита",
                subtitle = "Детальный просмотр и тестовые события аналитики.",
            )
        }

        when (val current = state) {
            LoadState.Loading -> item { CircularProgressIndicator() }
            is LoadState.Error -> item { Text(current.message) }
            is LoadState.Success -> {
                item {
                    BeatCard(beat = current.data.beat)
                }

                item {
                    OutlinedButton(
                        onClick = { player.togglePlayback(current.data.beat.audioPreviewUrl) },
                    ) {
                        Text(
                            when {
                                player.isLoading -> "Загрузка превью..."
                                player.isPlaying -> "Остановить превью"
                                else -> "Слушать MP3-превью"
                            }
                        )
                    }
                }

                if (!player.errorMessage.isNullOrBlank()) {
                    item {
                        Text(player.errorMessage!!)
                    }
                }

                items(
                    listOf(
                        DashboardMetricUi("Прослушивания", current.data.beat.plays.toString(), "Текущее значение"),
                        DashboardMetricUi("Лайки", current.data.likesCount.toString(), "Текущее значение"),
                        DashboardMetricUi("Покупки", current.data.purchasesCount.toString(), "Текущее значение"),
                        DashboardMetricUi("Выручка", "${current.data.revenueRubles} ₽", "Обновлено: ${current.data.updatedAt}"),
                    )
                ) { metric ->
                    DashboardMetricCard(metric = metric)
                }

                item {
                    Button(onClick = { viewModel.simulatePlay(beatId) }) {
                        Text("Добавить прослушивание")
                    }
                }
                item {
                    Button(onClick = { viewModel.simulateLike(beatId) }) {
                        Text("Добавить лайк")
                    }
                }
                item {
                    Button(onClick = { viewModel.simulatePurchase(beatId) }) {
                        Text("Добавить покупку")
                    }
                }
            }
        }
    }
}
