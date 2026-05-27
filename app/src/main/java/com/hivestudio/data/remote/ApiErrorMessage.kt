package com.hivestudio.data.remote

import com.hivestudio.data.session.SessionEventBus
import com.hivestudio.data.session.SessionStore
import retrofit2.HttpException

fun Throwable.toUserMessage(defaultMessage: String): String =
    when (this) {
        is HttpException -> when (code()) {
            401 -> {
                SessionStore.clear()
                SessionEventBus.requestLogin()
                "Требуется вход в профиль продюсера"
            }
            404 -> "Ресурс не найден"
            else -> message()
        } ?: defaultMessage
        else -> message ?: defaultMessage
    }
