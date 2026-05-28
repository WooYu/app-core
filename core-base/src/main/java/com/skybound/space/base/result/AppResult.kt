package com.skybound.space.base.result

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
    val isLoading: Boolean get() = this is Loading
}

sealed class AppError {
    data class Network(val code: Int, val message: String) : AppError()
    data class Server(val code: Int, val message: String) : AppError()
    data class Local(val cause: Throwable) : AppError()
    data object Unauthorized : AppError()
    data object Unknown : AppError()
}
