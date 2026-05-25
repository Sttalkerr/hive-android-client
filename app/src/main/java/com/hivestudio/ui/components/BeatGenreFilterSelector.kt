package com.hivestudio.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BeatGenreFilterSelector(
    genres: List<String>,
    selectedGenre: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FilterChip(
            selected = selectedGenre == null,
            onClick = { onSelected(null) },
            label = { Text("Все жанры") },
            colors = FilterChipDefaults.filterChipColors(
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        )
        genres.forEach { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onSelected(genre) },
                label = { Text(genre) },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}
