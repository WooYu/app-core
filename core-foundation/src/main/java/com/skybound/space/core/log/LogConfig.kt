package com.skybound.space.core.log

import timber.log.Timber

enum class LogLevel { DEBUG, INFO, WARN, ERROR, NONE }

data class LogConfig(
    val minLevel: LogLevel = LogLevel.DEBUG,
    val crashReporting: Boolean = false
) {
    fun applyToTimber(isDebug: Boolean) {
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
        // Production tree is planted by the App layer (e.g. Crashlytics)
    }
}
