package com.hivestudio.ui.screens.beats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.components.BeatCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.preview.HiveStudioPreviewData

@Composable
fun BeatsScreen() {
    var query by rememberSaveable { mutableStateOf("") }
    val beats = HiveStudioPreviewData.beats.filter {
        it.title.contains(query, ignoreCase = true) || it.genre.contains(query, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Мои биты",
                subtitle = "Каталог продюсера с локальным поиском по названию и жанру.",
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

        items(beats) { beat ->
            BeatCard(beat = beat)
        }
    }
}
