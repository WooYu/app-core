package com.skybound.space.core

import android.app.Application
import com.skybound.space.core.log.AppLoggerProvider
import com.skybound.space.core.log.LogConfig
import com.skybound.space.core.log.LogLevel
import com.skybound.space.core.log.TimberLogger
import com.skybound.space.core.network.NetworkConfig
import com.skybound.space.core.network.NetworkManager

class CoreFoundation private constructor() {

    companion object {
        private var _networkManager: NetworkManager? = null
        val networkManager: NetworkManager
            get() = _networkManager ?: error("CoreFoundation not initialized. Call init() first.")

        fun init(app: Application, block: CoreFoundationBuilder.() -> Unit) {
            val builder = CoreFoundationBuilder().apply(block)
            builder.logConfig?.applyToTimber(isDebug = builder.isDebug)
            if (builder.useTimberLogger) {
                AppLoggerProvider.setLogger(TimberLogger())
            }
            builder.networkConfig?.let { _networkManager = NetworkManager(it) }
        }
    }
}

class CoreFoundationBuilder {
    var isDebug: Boolean = false
    var useTimberLogger: Boolean = true
    internal var networkConfig: NetworkConfig? = null
    internal var logConfig: LogConfig? = null

    fun network(block: NetworkConfigBuilder.() -> Unit) {
        networkConfig = NetworkConfigBuilder().apply(block).build()
    }

    fun logging(block: LogConfigBuilder.() -> Unit) {
        logConfig = LogConfigBuilder().apply(block).build()
    }
}

class NetworkConfigBuilder {
    var baseUrl: String = ""
    var connectTimeout: Long = 30
    var readTimeout: Long = 30
    var enableLogging: Boolean = false

    fun build() = NetworkConfig(
        baseUrl = baseUrl,
        connectTimeoutSeconds = connectTimeout,
        readTimeoutSeconds = readTimeout,
        enableLogging = enableLogging
    )
}

class LogConfigBuilder {
    var minLevel: LogLevel = LogLevel.DEBUG
    var crashReporting: Boolean = false

    fun build() = LogConfig(minLevel = minLevel, crashReporting = crashReporting)
}
