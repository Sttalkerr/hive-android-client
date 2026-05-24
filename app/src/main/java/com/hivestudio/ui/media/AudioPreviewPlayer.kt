package com.hivestudio.ui.media

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AudioPreviewPlayer(
    context: Context,
) {
    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var currentUrl: String? = null

    var isLoading by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun togglePlayback(url: String?) {
        if (url.isNullOrBlank()) {
            errorMessage = "У этого бита нет доступного MP3-превью"
            return
        }

        if (isPlaying && currentUrl == url) {
            stop()
        } else {
            play(url)
        }
    }

    fun stop() {
        mediaPlayer?.runCatching {
            stop()
        }
        releasePlayer()
    }

    fun release() {
        releasePlayer()
    }

    private fun play(url: String) {
        releasePlayer()
        this.errorMessage = null
        this.isLoading = true
        currentUrl = url

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener {
                this@AudioPreviewPlayer.isLoading = false
                this@AudioPreviewPlayer.isPlaying = true
                it.start()
            }
            setOnCompletionListener {
                releasePlayer()
            }
            setOnErrorListener { _, _, _ ->
                this@AudioPreviewPlayer.errorMessage = "Не удалось воспроизвести MP3-превью"
                releasePlayer()
                true
            }
            runCatching {
                setDataSource(appContext, android.net.Uri.parse(url))
                prepareAsync()
            }.onFailure {
                this@AudioPreviewPlayer.errorMessage = it.message ?: "Не удалось открыть MP3-превью"
                releasePlayer()
            }
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentUrl = null
        isLoading = false
        isPlaying = false
    }
}
