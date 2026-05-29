package com.skybound.space.data.repository

import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import com.skybound.space.core.network.BaseResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BaseRepositoryTest {

    private val repo = object : BaseRepository() {
        suspend fun <T> callApi(block: suspend () -> BaseResponse<T>) = safeApiCall(block)
        suspend fun <T> callDb(block: suspend () -> T) = safeDbCall(block)
    }

    @Test
    fun `safeApiCall returns Success when response isSuccess`() = runTest {
        val result = repo.callApi {
            BaseResponse(code = 200, message = "ok", data = "payload")
        }
        assertEquals(AppResult.Success("payload"), result)
    }

    @Test
    fun `safeApiCall returns Server error when code is not 200`() = runTest {
        val result = repo.callApi<String> {
            BaseResponse(code = 400, message = "bad request", data = null)
        }
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Server)
        assertEquals(400, (error as AppError.Server).code)
    }

    @Test
    fun `safeApiCall returns Local error on IOException`() = runTest {
        val result = repo.callApi<String> {
            throw IOException("timeout")
        }
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Local)
    }

    @Test
    fun `safeApiCall returns Unauthorized on HttpException 401`() = runTest {
        val result = repo.callApi<String> {
            throw HttpException(Response.error<String>(401, "".toResponseBody()))
        }
        assertEquals(AppResult.Failure(AppError.Unauthorized), result)
    }

    @Test
    fun `safeDbCall returns Success`() = runTest {
        val result = repo.callDb { 42 }
        assertEquals(AppResult.Success(42), result)
    }

    @Test
    fun `safeDbCall returns Local error on exception`() = runTest {
        val result = repo.callDb<Int> { throw RuntimeException("db error") }
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Local)
    }
}
