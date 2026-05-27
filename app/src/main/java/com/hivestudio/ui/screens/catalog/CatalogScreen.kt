package com.hivestudio.ui.screens.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Tune
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
import kotlinx.coroutines.delay

@Composable
fun CatalogScreen(
    onOpenBeat: (String) -> Unit,
    viewModel: CatalogViewModel = viewModel(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var sortType by rememberSaveable { mutableStateOf(BeatSortType.Newest) }
    var selectedGenre by rememberSaveable { mutableStateOf<String?>(null) }
    var priceFilter by rememberSaveable { mutableStateOf(BeatPriceFilter.Any) }
    var isSearchFocused by rememberSaveable { mutableStateOf(false) }
    var isSortPanelVisible by rememberSaveable { mutableStateOf(false) }
    var isFilterPanelVisible by rememberSaveable { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val genres by viewModel.availableGenres.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val showHistory = isSearchFocused && query.isBlank() && searchHistory.isNotEmpty()

    LaunchedEffect(query) {
        delay(250)
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
            ScreenHeader(title = "Каталог")
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isSearchFocused = it.isFocused },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    placeholder = { Text("Поиск по названию или жанру") },
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                )
                if (query.isNotBlank()) {
                    TextButton(
                        onClick = {
                            query = ""
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            isSearchFocused = false
                        },
                    ) {
                        Text("Очистить")
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = {
                        isSortPanelVisible = !isSortPanelVisible
                        if (isSortPanelVisible) isFilterPanelVisible = false
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sort,
                        contentDescription = null,
                    )
                    Text("Сортировка")
                }
                Button(
                    onClick = {
                        isFilterPanelVisible = !isFilterPanelVisible
                        if (isFilterPanelVisible) isSortPanelVisible = false
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                    )
                    Text("Фильтры")
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AnimatedVisibility(visible = isSortPanelVisible) {
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Сортировка")
                        BeatSortSelector(
                            selected = sortType,
                            onSelected = {
                                sortType = it
                                viewModel.updateSort(it)
                                isSortPanelVisible = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AnimatedVisibility(visible = isFilterPanelVisible) {
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text("Жанр")
                        BeatGenreFilterSelector(
                            genres = genres,
                            selectedGenre = selectedGenre,
                            onSelected = {
                                selectedGenre = it
                                viewModel.updateGenreFilter(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text("Цена")
                        BeatPriceFilterSelector(
                            selected = priceFilter,
                            onSelected = {
                                priceFilter = it
                                viewModel.updatePriceFilter(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        if (showHistory) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("История поиска")
                    TextButton(onClick = viewModel::clearHistory) {
                        Text("Очистить историю")
                    }
                }
            }

            items(searchHistory, span = { GridItemSpan(maxLineSpan) }, key = { it.beatId }) { entry ->
                Button(
                    onClick = {
                        query = entry.title
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        isSearchFocused = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("${entry.title} • ${entry.producerStageName}")
                }
            }
        }

        if (!showHistory) {
            when (val current = state) {
                LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(current.message)
                            Button(
                                onClick = viewModel::retryLastQuery,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Обновить")
                            }
                        }
                    }
                }
                is LoadState.Success -> {
                    if (current.data.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text("Ничего не найдено")
                                    Text("Попробуй изменить поисковый запрос или фильтры")
                                }
                            }
                        }
                    }
                    items(current.data, key = { it.id }) { beat ->
                        BeatCard(
                            beat = beat,
                            compact = true,
                            onOpenClick = {
                                viewModel.saveHistory(beat)
                                onOpenBeat(beat.id)
                            },
                        )
                    }
                }
            }
        }
    }
}
