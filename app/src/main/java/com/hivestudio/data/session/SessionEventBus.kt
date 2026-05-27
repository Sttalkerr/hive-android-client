package com.hivestudio.data.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionEventBus {
    private val _events = MutableSharedFlow<SessionEvent>(
        extraBufferCapacity = 1,
    )
    val events = _events.asSharedFlow()

    fun requestLogin() {
        _events.tryEmit(SessionEvent.LoginRequired)
    }
}

sealed interface SessionEvent {
    data object LoginRequired : SessionEvent
}
