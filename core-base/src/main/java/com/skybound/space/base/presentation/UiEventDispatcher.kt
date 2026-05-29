package com.skybound.space.base.presentation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Channel-backed dispatcher for one-shot UI events.
 *
 * Uses [Channel.UNLIMITED] so [send] never drops events regardless of how
 * quickly they are produced. Events remain in the channel until a collector
 * (Activity / Fragment) drains them; the ViewModel lifecycle ensures the
 * channel is garbage-collected together with the ViewModel.
 *
 * Collectors should use [collectWhenStarted] / [collectWhenResumed] so events
 * are only delivered while the UI is visible.
 */
class UiEventDispatcher<E : UiEvent> {
    private val _channel = Channel<E>(Channel.UNLIMITED)
    val flow: Flow<E> = _channel.receiveAsFlow()

    /**
     * Enqueues [event] for delivery to the current collector.
     * Always succeeds — [Channel.UNLIMITED] guarantees no buffer overflow.
     */
    fun send(event: E) {
        _channel.trySend(event)
    }
}
