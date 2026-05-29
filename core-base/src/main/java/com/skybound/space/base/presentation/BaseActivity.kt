package com.skybound.space.base.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity<S : UiState, E : UiEvent> : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected abstract fun renderState(state: S)
    protected abstract fun handleEvent(event: E)
}
