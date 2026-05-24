package com.hivestudio.ui.screens.beats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
fun BeatsScreen(
    viewModel: BeatsViewModel = viewModel(),
    onOpenBeat: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        viewModel.loadBeats(query.ifBlank { null })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Мои биты",
                subtitle = "Список загружается с сервера Hive Studio по API.",
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { androidx.compose.material3.Text("Поиск по названию или жанру") },
                singleLine = true,
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
                items(current.data) { beat ->
                    BeatCard(
                        beat = beat,
                        onDeleteClick = { viewModel.deleteBeat(beat.id) },
                        onOpenClick = { onOpenBeat(beat.id) },
                    )
                }
            }
        }
    }
}
