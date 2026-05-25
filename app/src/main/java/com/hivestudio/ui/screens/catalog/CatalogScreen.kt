package com.hivestudio.ui.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.hivestudio.ui.components.BeatGenreFilterSelector
import com.hivestudio.ui.components.BeatPriceFilterSelector
import com.hivestudio.ui.components.BeatSortSelector
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.model.BeatPriceFilter
import com.hivestudio.ui.model.BeatSortType
import com.hivestudio.ui.model.LoadState

@Composable
fun CatalogScreen(
    onOpenBeat: (String) -> Unit,
    viewModel: CatalogViewModel = viewModel(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var sortType by rememberSaveable { mutableStateOf(BeatSortType.Newest) }
    var selectedGenre by rememberSaveable { mutableStateOf<String?>(null) }
    var priceFilter by rememberSaveable { mutableStateOf(BeatPriceFilter.Any) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val genres by viewModel.availableGenres.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        viewModel.loadCatalog(query.ifBlank { null })
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ScreenHeader(
                title = "Каталог",
                subtitle = "Все биты продюсеров Hive Studio. Поиск работает по названию и жанру.",
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                label = { Text("Поиск по каталогу") },
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            BeatSortSelector(
                selected = sortType,
                onSelected = {
                    sortType = it
                    viewModel.updateSort(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            BeatGenreFilterSelector(
                genres = genres,
                selectedGenre = selectedGenre,
                onSelected = {
                    selectedGenre = it
                    viewModel.updateGenreFilter(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            BeatPriceFilterSelector(
                selected = priceFilter,
                onSelected = {
                    priceFilter = it
                    viewModel.updatePriceFilter(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        when (val current = state) {
            LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) { CircularProgressIndicator() }
            is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) { Text(current.message) }
            is LoadState.Success -> {
                if (current.data.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("По этому запросу в каталоге пока ничего не найдено")
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
