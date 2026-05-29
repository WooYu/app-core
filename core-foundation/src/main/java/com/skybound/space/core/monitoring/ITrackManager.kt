package com.skybound.space.core.monitoring

interface ITrackManager {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackScreen(screenName: String)
    fun setUserProperty(key: String, value: String)
}

object NoOpTrackManager : ITrackManager {
    override fun trackEvent(name: String, params: Map<String, Any>) = Unit
    override fun trackScreen(screenName: String) = Unit
    override fun setUserProperty(key: String, value: String) = Unit
}
