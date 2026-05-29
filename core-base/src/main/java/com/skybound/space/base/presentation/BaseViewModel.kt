package com.skybound.space.base.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : UiState, E : UiEvent>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _eventDispatcher = UiEventDispatcher<E>()
    val events: Flow<E> = _eventDispatcher.flow

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    protected fun sendEvent(event: E) {
        _eventDispatcher.send(event)
    }
}
