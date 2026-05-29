package com.skybound.space.base.presentation

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Concrete test implementations ---

    data class TestState(val count: Int = 0) : UiState

    sealed class TestEvent : UiEvent {
        data object Increment : TestEvent()
    }

    class TestViewModel : BaseViewModel<TestState, TestEvent>(TestState()) {
        fun increment() {
            updateState { copy(count = count + 1) }
        }
        fun fireEvent() {
            sendEvent(TestEvent.Increment)
        }
    }

    // --- Tests ---

    @Test
    fun `initial state is emitted`() = runTest {
        val vm = TestViewModel()
        assertEquals(TestState(count = 0), vm.uiState.value)
    }

    @Test
    fun `updateState emits new state`() = runTest {
        val vm = TestViewModel()
        vm.uiState.test {
            assertEquals(TestState(0), awaitItem())
            vm.increment()
            assertEquals(TestState(1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent delivers event via flow`() = runTest {
        val vm = TestViewModel()
        vm.events.test {
            vm.fireEvent()
            assertEquals(TestEvent.Increment, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple state updates are sequential`() = runTest {
        val vm = TestViewModel()
        repeat(3) { vm.increment() }
        assertEquals(3, vm.uiState.value.count)
    }
}
