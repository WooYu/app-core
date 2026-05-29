package com.skybound.space.core.monitoring

interface IPerformanceMonitor {
    fun startTrace(name: String)
    fun stopTrace(name: String)
    fun putMetric(traceName: String, metricName: String, value: Long)
}

object NoOpPerformanceMonitor : IPerformanceMonitor {
    override fun startTrace(name: String) = Unit
    override fun stopTrace(name: String) = Unit
    override fun putMetric(traceName: String, metricName: String, value: Long) = Unit
}
