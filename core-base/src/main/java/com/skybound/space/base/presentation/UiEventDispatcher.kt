package com.skybound.space.base.presentation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class UiEventDispatcher<E : UiEvent> {
    private val _channel = Channel<E>(Channel.BUFFERED)
    val flow: Flow<E> = _channel.receiveAsFlow()

    fun send(event: E) {
        _channel.trySend(event)
    }
}
