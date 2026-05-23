package com.hivestudio.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.components.BeatCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.model.AddBeatDraftUi
import com.hivestudio.ui.model.BeatCardUi

@Composable
fun AddBeatScreen() {
    var draft by rememberSaveable { mutableStateOf(AddBeatDraftUi()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Добавить бит",
                subtitle = "Черновик формы для будущей загрузки MP3-файла и отправки на сервер.",
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = draft.title,
                        onValueChange = { draft = draft.copy(title = it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Название") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.genre,
                        onValueChange = { draft = draft.copy(genre = it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Жанр") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.bpm,
                        onValueChange = { draft = draft.copy(bpm = it.filter(Char::isDigit)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("BPM") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.priceRubles,
                        onValueChange = { draft = draft.copy(priceRubles = it.filter(Char::isDigit)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Цена лицензии, ₽") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.description,
                        onValueChange = { draft = draft.copy(description = it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Описание") },
                        minLines = 3,
                    )
                    Text(
                        text = "Поле загрузки MP3 подключим следующим шагом после сетевого слоя клиента и API сервера. Все цены в проекте считаются в рублях.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = draft.canSave,
                    ) {
                        Text("Сохранить черновик бита")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Предпросмотр карточки",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    BeatCard(
                        beat = BeatCardUi(
                            title = draft.title.ifBlank { "Новый бит" },
                            genre = draft.genre.ifBlank { "Жанр" },
                            bpm = draft.bpm.toIntOrNull() ?: 0,
                            priceRubles = draft.priceRubles.toIntOrNull() ?: 0,
                            description = draft.description.ifBlank { "Описание будущего релиза появится здесь." },
                            plays = 0,
                        )
                    )
                }
            }
        }
    }
}
