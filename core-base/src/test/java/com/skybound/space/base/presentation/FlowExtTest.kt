package com.skybound.space.base.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for [collectWhenStarted] and [collectWhenResumed] extensions.
 *
 * Uses [UnconfinedTestDispatcher] so coroutines (including repeatOnLifecycle's
 * internal lifecycle observer) run eagerly — no manual advanceUntilIdle() needed
 * and no risk of a livelock with StandardTestDispatcher.
 *
 * For emissions when there is NO active subscriber (lifecycle below the required
 * state), use [backgroundScope].launch to avoid suspending the test coroutine.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlowExtTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var lifecycleOwner: TestLifecycleOwner

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        lifecycleOwner = TestLifecycleOwner(
            initialState = Lifecycle.State.INITIALIZED,
            coroutineDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── collectWhenStarted ────────────────────────────────────────────────────

    @Test
    fun `collectWhenStarted - does not collect before STARTED`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenStarted(lifecycleOwner) { collected.add(it) }

        // No subscriber (INITIALIZED < STARTED) — use backgroundScope to avoid blocking
        backgroundScope.launch { flow.emit(1) }

        assertEquals(emptyList<Int>(), collected)
    }

    @Test
    fun `collectWhenStarted - collects when STARTED`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenStarted(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)

        flow.emit(1)
        flow.emit(2)

        assertEquals(listOf(1, 2), collected)
    }

    @Test
    fun `collectWhenStarted - stops collecting when STOPPED`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenStarted(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        flow.emit(1)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        backgroundScope.launch { flow.emit(2) } // no subscriber after STOP

        assertEquals(listOf(1), collected)
    }

    @Test
    fun `collectWhenStarted - resumes collecting after restart`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenStarted(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        flow.emit(1)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        backgroundScope.launch { flow.emit(2) } // not collected

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        flow.emit(3) // collected again

        assertEquals(listOf(1, 3), collected)
    }

    // ── collectWhenResumed ────────────────────────────────────────────────────

    @Test
    fun `collectWhenResumed - does not collect when only STARTED`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenResumed(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        backgroundScope.launch { flow.emit(1) } // STARTED < RESUMED — no subscriber

        assertEquals(emptyList<Int>(), collected)
    }

    @Test
    fun `collectWhenResumed - collects when RESUMED`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenResumed(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        flow.emit(1)
        flow.emit(2)

        assertEquals(listOf(1, 2), collected)
    }

    @Test
    fun `collectWhenResumed - stops collecting when PAUSED`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenResumed(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        flow.emit(1)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        backgroundScope.launch { flow.emit(2) } // no subscriber after PAUSE

        assertEquals(listOf(1), collected)
    }

    @Test
    fun `collectWhenResumed - resumes collecting after re-resume`() = runTest(testDispatcher) {
        val flow = MutableSharedFlow<Int>()
        val collected = mutableListOf<Int>()

        flow.collectWhenResumed(lifecycleOwner) { collected.add(it) }

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        flow.emit(1)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        backgroundScope.launch { flow.emit(2) } // not collected

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        flow.emit(3) // collected again

        assertEquals(listOf(1, 3), collected)
    }
}
