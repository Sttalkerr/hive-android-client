package com.hivestudio.ui.screens.beats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    onOpenAddBeat: () -> Unit,
    onOpenBeat: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadBeats(query = null)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ScreenHeader(title = "Мои биты")
                FilledIconButton(
                    onClick = onOpenAddBeat,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Создать бит",
                    )
                }
            }
        }

        when (val current = state) {
            LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) { CircularProgressIndicator() }
            is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) { Text(current.message) }
            is LoadState.Success -> {
                if (current.data.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("У тебя пока нет битов по этому фильтру")
                    }
                }
                items(current.data, key = { it.id }) { beat ->
                    BeatCard(
                        beat = beat,
                        compact = true,
                        onOpenClick = { onOpenBeat(beat.id) },
                    )
                }
            }
        }
    }
}
