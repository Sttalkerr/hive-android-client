package com.hivestudio.ui.model

data class AddBeatDraftUi(
    val title: String = "",
    val genre: String = "",
    val bpm: String = "",
    val priceRubles: String = "",
    val description: String = "",
) {
    val canSave: Boolean
        get() = title.isNotBlank() &&
            genre.isNotBlank() &&
            bpm.isNotBlank() &&
            priceRubles.isNotBlank() &&
            description.isNotBlank()
}
