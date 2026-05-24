package com.hivestudio.data.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object CatalogRefreshBus {
    private val events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val flow = events.asSharedFlow()

    fun notifyChanged() {
        events.tryEmit(Unit)
    }
}
