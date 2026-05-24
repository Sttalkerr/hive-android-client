package com.hivestudio.ui.model

data class AddBeatDraftUi(
    val title: String = "",
    val genre: String = "",
    val bpm: String = "",
    val priceRubles: String = "",
    val mp3FileName: String = "",
    val coverImageFileName: String = "",
    val description: String = "",
) {
    val canSave: Boolean
        get() = title.isNotBlank() &&
            genre.isNotBlank() &&
            bpm.isNotBlank() &&
            priceRubles.isNotBlank() &&
            mp3FileName.isNotBlank() &&
            coverImageFileName.isNotBlank() &&
            description.isNotBlank()
}
