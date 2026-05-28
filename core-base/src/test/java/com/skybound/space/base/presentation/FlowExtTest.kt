package com.skybound.space.base.presentation

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowExtTest {

    @Test
    fun `flow emits expected values via turbine`() = runTest {
        val flow = MutableStateFlow(0)
        flow.test {
            assertEquals(0, awaitItem())
            flow.value = 1
            assertEquals(1, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
