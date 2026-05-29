package com.skybound.space.domain.usecase

import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers
) {
    operator fun invoke(params: P): Flow<AppResult<R>> =
        execute(params)
            .catch { e -> emit(AppResult.Failure(AppError.Local(e))) }
            .flowOn(dispatchers.io)

    protected abstract fun execute(params: P): Flow<AppResult<R>>
}
