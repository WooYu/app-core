package com.skybound.space.base.presentation.viewmodel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified VM : ViewModel> Fragment.viewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factory = factoryProducer?.invoke()
    return lazy {
        val provider = if (factory != null) {
            ViewModelProvider(this as ViewModelStoreOwner, factory)
        } else {
            ViewModelProvider(this as ViewModelStoreOwner)
        }
        provider[VM::class.java]
    }
}

inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factory = factoryProducer?.invoke()
    return lazy {
        val provider = if (factory != null) {
            ViewModelProvider(this as ViewModelStoreOwner, factory)
        } else {
            ViewModelProvider(this as ViewModelStoreOwner)
        }
        provider[VM::class.java]
    }
}
