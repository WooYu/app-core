package com.skybound.space.core.log

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppLoggerTest {

    private val messages = mutableListOf<String>()

    private val testLogger = object : AppLogger {
        override fun d(tag: String, message: String) { messages.add("D/$tag: $message") }
        override fun i(tag: String, message: String) { messages.add("I/$tag: $message") }
        override fun w(tag: String, message: String) { messages.add("W/$tag: $message") }
        override fun e(tag: String, message: String, throwable: Throwable?) {
            messages.add("E/$tag: $message")
        }
    }

    @Before
    fun setUp() {
        messages.clear()
        AppLoggerProvider.setLogger(testLogger)
    }

    @Test
    fun `d logs debug message`() {
        AppLoggerProvider.d("TestTag", "debug message")
        assertEquals("D/TestTag: debug message", messages.first())
    }

    @Test
    fun `e logs error message`() {
        AppLoggerProvider.e("TestTag", "error message")
        assertEquals("E/TestTag: error message", messages.first())
    }

    @Test
    fun `no log when logger not set uses NoOp`() {
        AppLoggerProvider.setLogger(NoOpLogger)
        AppLoggerProvider.d("tag", "msg") // must not throw
        assertEquals(0, messages.size)
    }
}
