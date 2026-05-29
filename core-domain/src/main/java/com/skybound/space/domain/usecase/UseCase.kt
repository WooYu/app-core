package com.skybound.space.domain.usecase

import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

abstract class UseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: P): AppResult<R> =
        withContext(dispatchers.io) {
            try {
                execute(params)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppResult.Failure(AppError.Local(e))
            }
        }

    protected abstract suspend fun execute(params: P): AppResult<R>
}
