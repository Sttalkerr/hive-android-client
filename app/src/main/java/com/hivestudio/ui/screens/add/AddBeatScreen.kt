package com.hivestudio.ui.screens.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.BeatCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.model.AddBeatDraftUi
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.LoadState

@Composable
fun AddBeatScreen(
    viewModel: AddBeatViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uploadState = viewModel.uploadState.collectAsStateWithLifecycle().value

    val title = rememberSaveable { mutableStateOf("") }
    val genre = rememberSaveable { mutableStateOf("") }
    val bpm = rememberSaveable { mutableStateOf("") }
    val priceRubles = rememberSaveable { mutableStateOf("") }
    val mp3FileName = rememberSaveable { mutableStateOf("") }
    val coverImageFileName = rememberSaveable { mutableStateOf("") }
    val description = rememberSaveable { mutableStateOf("") }
    var mp3UriString by rememberSaveable { mutableStateOf<String?>(null) }
    var coverUriString by rememberSaveable { mutableStateOf<String?>(null) }

    val mp3Picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            mp3UriString = uri.toString()
            mp3FileName.value = resolveDisplayName(context, uri) ?: "selected-track.mp3"
        }
    }
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coverUriString = uri.toString()
            coverImageFileName.value = resolveDisplayName(context, uri) ?: "selected-cover.jpg"
        }
    }

    val mp3Uri = mp3UriString?.let(Uri::parse)
    val coverUri = coverUriString?.let(Uri::parse)

    val draft = AddBeatDraftUi(
        title = title.value,
        genre = genre.value,
        bpm = bpm.value,
        priceRubles = priceRubles.value,
        mp3FileName = mp3FileName.value,
        coverImageFileName = coverImageFileName.value,
        description = description.value,
    )

    LaunchedEffect(uploadState) {
        val successMessage = (uploadState as? LoadState.Success)?.data.orEmpty()
        if (successMessage.contains("успешно загружен")) {
            title.value = ""
            genre.value = ""
            bpm.value = ""
            priceRubles.value = ""
            mp3FileName.value = ""
            coverImageFileName.value = ""
            description.value = ""
            mp3UriString = null
            coverUriString = null
            viewModel.resetUploadState()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Добавить бит",
                subtitle = "Форма отправляет MP3 и квадратную обложку на сервер в формате multipart.",
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
                        onValueChange = { title.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Название") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.genre,
                        onValueChange = { genre.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Жанр") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.bpm,
                        onValueChange = { bpm.value = it.filter(Char::isDigit) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("BPM") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.priceRubles,
                        onValueChange = { priceRubles.value = it.filter(Char::isDigit) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Цена лицензии, ₽") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draft.mp3FileName,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("MP3 файл") },
                        supportingText = { Text("Выбери настоящий mp3-файл с устройства") },
                        readOnly = true,
                        singleLine = true,
                    )
                    Button(
                        onClick = { mp3Picker.launch("audio/*") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Выбрать MP3")
                    }
                    OutlinedTextField(
                        value = draft.coverImageFileName,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Квадратная обложка") },
                        supportingText = { Text("Выбери квадратное изображение обложки") },
                        readOnly = true,
                        singleLine = true,
                    )
                    Button(
                        onClick = { coverPicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Выбрать обложку")
                    }
                    OutlinedTextField(
                        value = draft.description,
                        onValueChange = { description.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Описание") },
                        minLines = 3,
                    )
                    Text(
                        text = "У бита обязательно должны быть два файла: mp3 и квадратная обложка. После выбора файлов можно сразу отправить их на сервер.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    when (val state = uploadState) {
                        LoadState.Loading -> CircularProgressIndicator()
                        is LoadState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                        is LoadState.Success -> Text(state.data, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = {
                            viewModel.uploadBeat(
                                context = context,
                                draft = draft,
                                mp3Uri = mp3Uri,
                                coverUri = coverUri,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = draft.canSave,
                    ) {
                        Text("Загрузить бит на сервер")
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
                            id = "preview-draft",
                            title = draft.title.ifBlank { "Новый бит" },
                            genre = draft.genre.ifBlank { "Жанр" },
                            bpm = draft.bpm.toIntOrNull() ?: 0,
                            priceRubles = draft.priceRubles.toIntOrNull() ?: 0,
                            coverImageFileName = draft.coverImageFileName.ifBlank { "square-cover.jpg" },
                            localCoverUri = coverUriString,
                            description = draft.description.ifBlank { "Описание будущего релиза появится здесь." },
                            plays = 0,
                        )
                    )
                }
            }
        }
    }
}

private fun resolveDisplayName(context: android.content.Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && it.moveToFirst()) {
            return it.getString(index)
        }
    }
    return null
}
