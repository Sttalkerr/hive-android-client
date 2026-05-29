package com.hivestudio.ui.screens.editbeat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.BeatCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.model.LoadState

@Composable
fun EditBeatScreen(
    beatId: String,
    onSaved: () -> Unit,
    viewModel: EditBeatViewModel = viewModel(),
) {
    val beatState by viewModel.beatState.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var title by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    var bpm by rememberSaveable { mutableStateOf("") }
    var priceRubles by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(beatId) {
        viewModel.loadBeat(beatId)
    }

    LaunchedEffect(beatState) {
        val beat = (beatState as? LoadState.Success)?.data ?: return@LaunchedEffect
        if (title.isBlank()) title = beat.title
        if (genre.isBlank()) genre = beat.genre
        if (bpm.isBlank()) bpm = beat.bpm.toString()
        if (priceRubles.isBlank()) priceRubles = beat.priceRubles.toString()
        if (description.isBlank()) description = beat.description
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Редактирование бита",
                subtitle = "Здесь можно скорректировать метаданные релиза без повторной загрузки MP3 и обложки.",
                centered = true,
            )
        }

        when (val current = beatState) {
            LoadState.Loading -> item { }
            is LoadState.Error -> item { Text(current.message) }
            is LoadState.Success -> {
                item {
                    BeatCard(
                        beat = current.data,
                        compact = true,
                    )
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Название") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = genre,
                                onValueChange = { genre = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Жанр") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = bpm,
                                onValueChange = { bpm = it.filter(Char::isDigit) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("BPM") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = priceRubles,
                                onValueChange = { priceRubles = it.filter(Char::isDigit) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Цена, ₽") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Описание") },
                                minLines = 4,
                            )

                            Text(
                                text = "MP3 и обложка пока не редактируются в этой версии.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            when (val state = saveState) {
                                LoadState.Loading -> Text("Сохранение...")
                                is LoadState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                                is LoadState.Success -> if (state.data.isNotBlank()) {
                                    Text(state.data, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.updateBeat(
                                        beatId = beatId,
                                        title = title,
                                        genre = genre,
                                        bpm = bpm,
                                        priceRubles = priceRubles,
                                        description = description,
                                        onSaved = onSaved,
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Сохранить изменения")
                            }
                        }
                    }
                }
            }
        }
    }
}
