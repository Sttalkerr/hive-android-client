package com.hivestudio.data.repository

import com.hivestudio.data.remote.model.CreateBeatRequestDto
import com.hivestudio.ui.model.AddBeatDraftUi

fun AddBeatDraftUi.toCreateBeatRequestDto(): CreateBeatRequestDto =
    CreateBeatRequestDto(
        title = title.trim(),
        genre = genre.trim(),
        bpm = bpm.toIntOrNull() ?: 0,
        price = priceRubles.toDoubleOrNull() ?: 0.0,
        description = description.trim(),
        mp3FileName = "draft-upload.mp3",
    )
