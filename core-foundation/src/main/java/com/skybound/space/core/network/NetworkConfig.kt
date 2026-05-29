package com.skybound.space.core.network

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 30,
    val readTimeoutSeconds: Long = 30,
    val writeTimeoutSeconds: Long = 30,
    val enableLogging: Boolean = false,
    val certificatePins: List<String> = emptyList()
)
