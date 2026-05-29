package com.skybound.space.base.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment<S : UiState, E : UiEvent> : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        val vm = provideViewModel() ?: return
        vm.uiState.collectWhenStarted(viewLifecycleOwner) { renderState(it) }
        vm.events.collectWhenStarted(viewLifecycleOwner) { handleEvent(it) }
    }

    protected open fun provideViewModel(): BaseViewModel<S, E>? = null
    protected abstract fun renderState(state: S)
    protected abstract fun handleEvent(event: E)
}
