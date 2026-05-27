package com.hivestudio.ui.screens.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivestudio.data.remote.toUserMessage
import com.hivestudio.data.repository.AddBeatUploadRepository
import com.hivestudio.data.repository.CatalogRefreshBus
import com.hivestudio.ui.model.AddBeatDraftUi
import com.hivestudio.ui.model.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddBeatViewModel(
    private val repository: AddBeatUploadRepository = AddBeatUploadRepository(),
) : ViewModel() {
    private val _uploadState = MutableStateFlow<LoadState<UploadedBeatResult>>(LoadState.Success(UploadedBeatResult.EMPTY))
    val uploadState: StateFlow<LoadState<UploadedBeatResult>> = _uploadState.asStateFlow()

    fun uploadBeat(
        context: Context,
        draft: AddBeatDraftUi,
        mp3Uri: Uri?,
        coverUri: Uri?,
    ) {
        if (mp3Uri == null || coverUri == null) {
            _uploadState.value = LoadState.Error("Нужно выбрать MP3 и квадратную обложку")
            return
        }

        viewModelScope.launch {
            _uploadState.value = LoadState.Loading
            _uploadState.value = runCatching {
                val beat = repository.uploadBeat(context, draft, mp3Uri, coverUri)
                CatalogRefreshBus.notifyChanged()
                LoadState.Success(
                    UploadedBeatResult(
                        beatId = beat.id,
                        message = "Бит ${beat.title} успешно загружен",
                    )
                )
            }.getOrElse {
                LoadState.Error(it.toUserMessage("Не удалось загрузить бит"))
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = LoadState.Success(UploadedBeatResult.EMPTY)
    }
}

data class UploadedBeatResult(
    val beatId: String,
    val message: String,
) {
    companion object {
        val EMPTY = UploadedBeatResult(
            beatId = "",
            message = "",
        )
    }
}
