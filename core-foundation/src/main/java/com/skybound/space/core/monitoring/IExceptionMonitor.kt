package com.skybound.space.core.monitoring

interface IExceptionMonitor {
    fun recordException(throwable: Throwable)
    fun recordBreadcrumb(message: String, category: String = "app")
    fun setUserId(userId: String?)
}

object NoOpExceptionMonitor : IExceptionMonitor {
    override fun recordException(throwable: Throwable) = Unit
    override fun recordBreadcrumb(message: String, category: String) = Unit
    override fun setUserId(userId: String?) = Unit
}
