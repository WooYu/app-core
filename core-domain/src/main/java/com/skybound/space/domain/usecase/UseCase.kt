package com.skybound.space.domain.usecase

import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.withContext

abstract class UseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: P): AppResult<R> =
        withContext(dispatchers.io) { execute(params) }

    protected abstract suspend fun execute(params: P): AppResult<R>
}
