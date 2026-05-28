package com.skybound.space.base.result

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    @Test
    fun `Success holds data`() {
        val result = AppResult.Success("hello")
        assertEquals("hello", result.data)
    }

    @Test
    fun `Failure holds error`() {
        val error = AppError.Unknown
        val result = AppResult.Failure(error)
        assertEquals(error, result.error)
    }

    @Test
    fun `map transforms Success value`() {
        val result: AppResult<Int> = AppResult.Success(5)
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Success(10), mapped)
    }

    @Test
    fun `map preserves Failure unchanged`() {
        val error = AppError.Unknown
        val result: AppResult<Int> = AppResult.Failure(error)
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Failure(error), mapped)
    }

    @Test
    fun `map preserves Loading unchanged`() {
        val result: AppResult<Int> = AppResult.Loading
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Loading, mapped)
    }

    @Test
    fun `getOrNull returns data for Success`() {
        val result = AppResult.Success(42)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for Failure`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.Unknown)
        assertNull(result.getOrNull())
    }

    @Test
    fun `isSuccess returns true only for Success`() {
        assertTrue(AppResult.Success(1).isSuccess)
        assertTrue(!AppResult.Failure(AppError.Unknown).isSuccess)
        assertTrue(!AppResult.Loading.isSuccess)
    }

    @Test
    fun `onSuccess callback invoked for Success`() {
        var called = false
        AppResult.Success("x").onSuccess { called = true }
        assertTrue(called)
    }

    @Test
    fun `onSuccess callback not invoked for Failure`() {
        var called = false
        AppResult.Failure(AppError.Unknown).onSuccess { called = true }
        assertTrue(!called)
    }

    @Test
    fun `onFailure callback invoked for Failure`() {
        var called = false
        AppResult.Failure(AppError.Unknown).onFailure { called = true }
        assertTrue(called)
    }

    // --- getOrDefault ---
    @Test
    fun `getOrDefault returns data for Success`() {
        val result = AppResult.Success(10)
        assertEquals(10, result.getOrDefault(99))
    }

    @Test
    fun `getOrDefault returns default for Failure`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.Unknown)
        assertEquals(99, result.getOrDefault(99))
    }

    @Test
    fun `getOrDefault returns default for Loading`() {
        val result: AppResult<Int> = AppResult.Loading
        assertEquals(99, result.getOrDefault(99))
    }

    // --- onLoading ---
    @Test
    fun `onLoading callback invoked for Loading`() {
        var called = false
        AppResult.Loading.onLoading { called = true }
        assertTrue(called)
    }

    @Test
    fun `onLoading callback not invoked for Success`() {
        var called = false
        AppResult.Success("x").onLoading { called = true }
        assertTrue(!called)
    }

    // --- isFailure / isLoading ---
    @Test
    fun `isFailure returns true only for Failure`() {
        assertTrue(AppResult.Failure(AppError.Unknown).isFailure)
        assertTrue(!AppResult.Success(1).isFailure)
        assertTrue(!AppResult.Loading.isFailure)
    }

    @Test
    fun `isLoading returns true only for Loading`() {
        assertTrue(AppResult.Loading.isLoading)
        assertTrue(!AppResult.Success(1).isLoading)
        assertTrue(!AppResult.Failure(AppError.Unknown).isLoading)
    }

    // --- mapSuspend ---
    @Test
    fun `mapSuspend transforms Success value`() = runTest {
        val result: AppResult<Int> = AppResult.Success(5)
        val mapped = result.mapSuspend { it * 3 }
        assertEquals(AppResult.Success(15), mapped)
    }

    @Test
    fun `mapSuspend preserves Failure`() = runTest {
        val error = AppError.Unknown
        val result: AppResult<Int> = AppResult.Failure(error)
        val mapped = result.mapSuspend { it * 3 }
        assertEquals(AppResult.Failure(error), mapped)
    }

    @Test
    fun `mapSuspend preserves Loading`() = runTest {
        val result: AppResult<Int> = AppResult.Loading
        val mapped = result.mapSuspend { it * 3 }
        assertEquals(AppResult.Loading, mapped)
    }
}
