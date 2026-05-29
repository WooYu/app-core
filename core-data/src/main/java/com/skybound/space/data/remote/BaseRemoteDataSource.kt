package com.skybound.space.data.remote

import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import com.skybound.space.core.network.BaseResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRemoteDataSource {

    protected suspend fun <T> safeCall(
        call: suspend () -> BaseResponse<T>
    ): AppResult<T> = try {
        val response = call()
        val data = response.data
        if (response.isSuccess && data != null) {
            AppResult.Success(data)
        } else {
            AppResult.Failure(AppError.Server(response.code, response.message))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        if (e.code() == 401) AppResult.Failure(AppError.Unauthorized)
        else AppResult.Failure(AppError.Network(e.code(), e.message()))
    } catch (e: IOException) {
        AppResult.Failure(AppError.Local(e))
    }
}
