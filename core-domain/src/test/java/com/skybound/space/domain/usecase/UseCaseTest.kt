package com.skybound.space.domain.usecase

import app.cash.turbine.test
import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseTest {

    private val testDispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    private inner class DoubleUseCase : UseCase<Int, Int>(testDispatchers) {
        override suspend fun execute(params: Int): AppResult<Int> =
            AppResult.Success(params * 2)
    }

    private inner class FailingUseCase : UseCase<Unit, String>(testDispatchers) {
        override suspend fun execute(params: Unit): AppResult<String> =
            AppResult.Failure(AppError.Unknown)
    }

    private inner class StreamUseCase : FlowUseCase<Int, Int>(testDispatchers) {
        override fun execute(params: Int): Flow<AppResult<Int>> = flow {
            emit(AppResult.Loading)
            emit(AppResult.Success(params * 3))
        }
    }

    @Test
    fun `UseCase returns Success with doubled value`() = runTest {
        val result = DoubleUseCase()(5)
        assertEquals(AppResult.Success(10), result)
    }

    @Test
    fun `UseCase returns Failure`() = runTest {
        val result = FailingUseCase()(Unit)
        assertTrue(result is AppResult.Failure)
    }

    @Test
    fun `FlowUseCase emits Loading then Success`() = runTest {
        StreamUseCase()(4).test {
            assertEquals(AppResult.Loading, awaitItem())
            assertEquals(AppResult.Success(12), awaitItem())
            awaitComplete()
        }
    }
}
